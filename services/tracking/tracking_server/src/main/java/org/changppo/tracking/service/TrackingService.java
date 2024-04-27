package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.changppo.tracking.jwt.TokenProvider;
import org.changppo.tracking.repository.CoordinatesRepository;
import org.changppo.tracking.repository.RedisRepository;
import org.changppo.tracking.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrackingService {

    private final TokenProvider tokenProvider;
    private final TrackingRepository trackingRepository;
    private final CoordinatesRepository coordinatesRepository;
    private final TrackingCacheService trackingCacheService;
    private final RedisRepository redisRepository;

    public GenerateTokenResponse generateToken(String apiKeyId, GenerateTokenRequest request) {
        if(request.getIdentifier().equals("DEFAULT")){ // DEFAULT 가 입력되면 처음 생성이라 생각하고, 랜덤 UUID 값을 생성
            request.setIdentifier(UUID.randomUUID().toString());
        }

        TrackingContext context = new TrackingContext(request.getIdentifier(), apiKeyId, request.getScope());
        String token = tokenProvider.createToken(context, request.getTokenExpiresIn());

        return new GenerateTokenResponse(token);
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

    public void finish(TrackingContext context) {
        TrackingRedisEntity trackingCache = checkTracking(context);

        // 끝남 처리
        Tracking tracking = trackingRepository.findById(trackingCache.trackingId()).orElseThrow(TrackingNotFoundException::new);
        tracking.updateEndedAt();

        trackingCacheService.updateTrackingCache(tracking);
        Tracking savedTracking = trackingRepository.save(tracking);

        // 벌크성 저장
        List<Object> objectList = redisRepository.findAll(savedTracking.getId());

        List<Coordinates> coordinatesList = objectList.stream()
                .filter(obj -> obj instanceof CoordinateRedisEntity)
                .map(obj -> (CoordinateRedisEntity) obj)
                .map(CoordinateRedisEntity::toCoordinates)
                .toList();

        coordinatesRepository.saveAll(coordinatesList);
    }

    public TrackingResponse getTracking(TrackingContext context) {
        TrackingRedisEntity trackingCache = checkTracking(context);

        Object latestCoordinatesObj = redisRepository.getTail(trackingCache.trackingId());
        if (latestCoordinatesObj instanceof CoordinateRedisEntity latestCoordinates) {
            return new TrackingResponse(latestCoordinates);
        } else {
            throw new IllegalArgumentException("좌표 데이터가 올바른 형식이 아닙니다.");
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