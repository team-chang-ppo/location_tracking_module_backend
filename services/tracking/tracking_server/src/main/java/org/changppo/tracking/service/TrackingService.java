package org.changppo.tracking.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.tracking.api.request.ConnectRequest;
import org.changppo.tracking.api.request.TrackingRequest;
import org.changppo.tracking.api.response.ConnectResponse;
import org.changppo.tracking.api.response.TrackingResponse;
import org.changppo.tracking.domain.Authority;
import org.changppo.tracking.domain.Coordinates;
import org.changppo.tracking.domain.Tracking;
import org.changppo.tracking.domain.TrackingContext;
import org.changppo.tracking.jwt.TokenProvider;
import org.changppo.tracking.repository.CoordinatesRepository;
import org.changppo.tracking.repository.TrackingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TrackingService {

    private final TokenProvider tokenProvider;
    private final TrackingRepository trackingRepository;
    private final CoordinatesRepository coordinatesRepository;

    public ConnectResponse connect(ConnectRequest request) {
        Tracking tracking = ConnectRequest.toEntity(request);

        // 같은 배달기사라도, 식별자인 id가 다르기에 id가 겹치면 오류를 발생시킨다.
        try {
            trackingRepository.insert(tracking);
        } catch (Exception e) {
            throw new RuntimeException("같은 정보가 존재"); // 409 error
        }

        TrackingContext context = new TrackingContext(request.getIdentifier(), Authority.BASIC.name());
        String token = tokenProvider.createToken(context);

        return new ConnectResponse(token);
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
                .orElseThrow(() -> new RuntimeException("Coordinates 값이 존재하지 않음."));
    }

    private Tracking checkTracking(String trackingId) {
        Tracking tracking = trackingRepository.findById(trackingId)
                .orElseThrow(()->new RuntimeException("존재하지 않는 tracking 정보")); // 404 error

        if(tracking.getEndedAt() != null) {
            throw new RuntimeException("이미 종료된 tracking"); // 400 error
        }
        return tracking;
    }
}
