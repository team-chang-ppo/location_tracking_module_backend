package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.api.request.GenerateTokenRequest;
import org.changppo.tracking.api.request.StartTrackingRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.GenerateTokenResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.Coordinates;
import org.changppo.tracking.domain.Tracking;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.exception.*;
import org.changppo.tracking.jwt.TokenProvider;
import org.changppo.tracking.repository.CoordinatesRepository;
import org.changppo.tracking.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrackingService {

    private final TokenProvider tokenProvider;
    private final TrackingRepository trackingRepository;
    private final CoordinatesRepository coordinatesRepository;

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

        try { // identifier 가 겹치면 오류를 발생
            trackingRepository.insert(tracking);
        } catch (Exception e) {
            throw new TrackingDuplicateException(); // 409 error
        }
    }

    public void tracking(TrackingRequest request, TrackingContext context) {
        Tracking tracking = checkTracking(context);

        Coordinates coordinates = TrackingRequest.toCoordinatesEntity(request, tracking.getId());
        coordinatesRepository.save(coordinates);
    }

    public void finish(TrackingContext context) {
        Tracking tracking = checkTracking(context);

        tracking.updateEndedAt(LocalDateTime.now().plusHours(9));

        trackingRepository.save(tracking);
    }

    public TrackingResponse getTracking(TrackingContext context) {
        Tracking tracking = checkTracking(context);

        return coordinatesRepository.findByTrackingIdOrderByCreatedAtDesc(tracking.getId())
                .stream().findFirst()
                .map(TrackingResponse::new)
                .orElseThrow(CoordinatesNotFoundException::new);
    }

    /**
     * 1. trackingId로 해당 tracking이 존재하는지 검사
     * 2. 해당 tracking 정보를 볼 수 있는지 검사 (api-key-id가 동일한지)
     * 3. 이미 끝난 tracking 정보가 아닌지 검사
     */
    private Tracking checkTracking(TrackingContext context) {
        Tracking tracking = trackingRepository.findById(context.trackingId())
                .orElseThrow(TrackingNotFoundException::new); // 404 error

        if(!tracking.getApiKeyId().equals(context.apiKeyId())) {
            throw new ApiKeyIdIsNotMatchedException(); // 401 error
        }

        if(tracking.getEndedAt() != null) {
            throw new TrackingAlreadyExitedException(); // 400 error
        }
        return tracking;
    }


}