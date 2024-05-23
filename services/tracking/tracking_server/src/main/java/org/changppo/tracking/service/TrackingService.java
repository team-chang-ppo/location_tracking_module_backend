package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.commons.ResponseBody;
import org.changppo.commons.SuccessResponseBody;
import org.changppo.tracking.api.request.GenerateTokenRequest;
import org.changppo.tracking.api.request.StartTrackingRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.GenerateTokenResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.mongodb.Coordinates;
import org.changppo.tracking.domain.mongodb.Tracking;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.domain.redis.CoordinateRedisEntity;
import org.changppo.tracking.domain.redis.TrackingRedisEntity;
import org.changppo.tracking.exception.*;
import org.changppo.tracking.feign.AccountClient;
import org.changppo.tracking.feign.ApikeyValidResponsePayload;
import org.changppo.tracking.jwt.exception.JwtTokenInvalidException;
import org.changppo.tracking.repository.RedisRepository;
import org.changppo.tracking.repository.TrackingRepository;
import org.changppo.tracking.util.RetryUtil;
import org.changppo.utils.jwt.apikey.ApiKeyJwtClaims;
import org.changppo.utils.jwt.apikey.ApiKeyJwtHandler;
import org.changppo.utils.jwt.tracking.TrackingJwtClaims;
import org.changppo.utils.jwt.tracking.TrackingJwtHandler;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrackingService {

    private final TrackingJwtHandler trackingJwtHandler;
    private final ApiKeyJwtHandler apiKeyJwtHandler;
    private final TrackingRepository trackingRepository;
    private final TrackingCacheService trackingCacheService;
    private final RedisRepository redisRepository;
    private final AsyncService asyncService;
    private final ScheduledExecutorService scheduler;
    private final AccountClient accountClient;

    public GenerateTokenResponse generateToken(String apiKeyToken, GenerateTokenRequest request) {
        if(request.getIdentifier().equals("DEFAULT")){ // DEFAULT 가 입력되면 처음 생성이라 생각하고, 랜덤 UUID 값을 생성
            request.setIdentifier(UUID.randomUUID().toString());
        }

        // API KEY TOKEN parsing
        ApiKeyJwtClaims apiKeyJwtClaims = apiKeyJwtHandler.parseToken(apiKeyToken)
                .orElseThrow(JwtTokenInvalidException::new); // invalid 토큰

        TrackingJwtClaims claims = new TrackingJwtClaims(
                apiKeyJwtClaims.getApikeyId(),
                apiKeyJwtClaims.getMemberId(),
                apiKeyJwtClaims.getGradeType(),
                request.getIdentifier(),
                request.getScope());

        // 요청
        this.validateApikey(apiKeyJwtClaims.getApikeyId());

        // 토큰 생성
        String token = trackingJwtHandler.createToken(claims);

        return new GenerateTokenResponse(token);
    }

    public void validateApikey(Long apikeyId) throws ApikeyInvalidException, UnexpectedServerErrorException {
        ResponseBody<ApikeyValidResponsePayload> response;
        try {
            response = accountClient.isApikeyIdValid(apikeyId);
        } catch (Exception e) {
            throw new UnexpectedServerErrorException();
        }

        if (!(response instanceof SuccessResponseBody<ApikeyValidResponsePayload> successResponseBody)) {
            throw new IllegalStateException("Unexpected response body type: " + response.getClass());
        }

        Boolean isValid = successResponseBody.getResult().getValid();
        if (!isValid) {
            throw new ApikeyInvalidException();
        }
    }

    public void startTracking(StartTrackingRequest request, TrackingContext context) {
        Tracking tracking = StartTrackingRequest.toEntity(context, request);
        log.debug("trackingId : {}", tracking.getId());

        try { // identifier 가 겹치면 오류를 발생
            trackingRepository.insert(tracking);
        } catch (Exception e) {
            throw new TrackingDuplicateException(); // 409 error
        }
    }

    public void tracking(TrackingRequest request, TrackingContext context) {
        TrackingRedisEntity trackingCache = checkTracking(context);

        Coordinates coordinates = TrackingRequest.toCoordinatesEntity(request, trackingCache.trackingId());

        CoordinateRedisEntity coordinateRedisEntity = new CoordinateRedisEntity(coordinates);
        redisRepository.rightPush(trackingCache.trackingId(), coordinateRedisEntity);
    }

    public void endTracking(TrackingContext context) {
        TrackingRedisEntity trackingCache = checkTracking(context);

        retryEndTrackingAsync(trackingCache.trackingId(), 0);
    }

    private void retryEndTrackingAsync(String trackingId, int attempt) {
        asyncService.processCoordinatesAsync(trackingId)
                .thenAccept(result -> {
                    log.debug("Redis -> MongoDB 저장 성공");
                })
                .exceptionally(e -> {
                    if (attempt < RetryUtil.MAX_ATTEMPTS) {
                        long delay = RetryUtil.calculateDelay(attempt);
                        scheduler.schedule(() -> retryEndTrackingAsync(trackingId, attempt + 1), delay, TimeUnit.MILLISECONDS);
                        log.info("재시도 {}", attempt);
                    } else {
                        log.info("최대 재시도 횟수를 초과했습니다. (Redis -> MongoDB 벌크성 저장 실패)");
                    }
                    return null;
                });
    }

    public TrackingResponse getTracking(TrackingContext context) {
        TrackingRedisEntity trackingCache = checkTracking(context);

        Object latestCoordinatesObj = redisRepository.getTail(trackingCache.trackingId());
        if (latestCoordinatesObj instanceof CoordinateRedisEntity latestCoordinates) {
            return new TrackingResponse(latestCoordinates);
        } else {
            throw new IllegalArgumentException("좌표가 존재하지 않거나, 좌표 데이터가 올바른 형식이 아닙니다.");
        }
    }

    private TrackingRedisEntity checkTracking(TrackingContext context) {
        TrackingRedisEntity trackingCache = trackingCacheService.getTrackingCache(context.trackingId());

        if(!trackingCache.apiKeyId().equals(context.apiKeyId())) {
            throw new ApiKeyIdIsNotMatchedException(); // 401 error
        }

        if(trackingCache.endedAt() != null) {
            trackingCacheService.deleteTrackingCache(context.trackingId()); // 종료된 tracking은 캐시에서 삭제
            throw new TrackingAlreadyExitedException(); // 410 error
        }
        return trackingCache;
    }
}