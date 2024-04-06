package org.changppo.tracking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.api.request.GenerateTokenRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.GenerateTokenResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.Coordinates;
import org.changppo.tracking.domain.Tracking;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.exception.CoordinatesNotFoundException;
import org.changppo.tracking.exception.TrackingAlreadyExitedException;
import org.changppo.tracking.exception.TrackingDuplicateException;
import org.changppo.tracking.exception.TrackingNotFoundException;
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
        String identifier = UUID.randomUUID().toString(); // 식별자 생성
        Tracking tracking = GenerateTokenRequest.toEntity(identifier, apiKeyId, request);

        try { // identifier 가 겹치면 오류를 발생
            trackingRepository.insert(tracking);
        } catch (Exception e) {
            throw new TrackingDuplicateException(); // 409 error
        }

        TrackingContext context = new TrackingContext(identifier, apiKeyId, request.getScope());
        String token = tokenProvider.createToken(context, request.getTokenExpiresIn());

        return new GenerateTokenResponse(token);
    }

    public void tracking(TrackingRequest request, String trackingId) {
        Tracking tracking = checkTracking(trackingId);

        Coordinates coordinates = TrackingRequest.toCoordinatesEntity(request, tracking.getId());
        coordinatesRepository.save(coordinates);
    }

    public void finish(String trackingId) {
        Tracking tracking = checkTracking(trackingId);

        tracking.updateEndedAt(LocalDateTime.now().plusHours(9));

        trackingRepository.save(tracking);
    }

    public TrackingResponse getTracking(String trackingId) {
        Tracking tracking = checkTracking(trackingId);

        return coordinatesRepository.findByTrackingIdOrderByCreatedAtDesc(tracking.getId())
                .stream().findFirst()
                .map(TrackingResponse::new)
                .orElseThrow(CoordinatesNotFoundException::new);
    }

    private Tracking checkTracking(String trackingId) {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(TrackingNotFoundException::new); // 404 error

        if(tracking.getEndedAt() != null) {
            throw new TrackingAlreadyExitedException(); // 400 error
        }
        return tracking;
    }
}