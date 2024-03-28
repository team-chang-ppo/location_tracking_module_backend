package org.changppo.tracking.api.request;

import lombok.Getter;
import org.changppo.tracking.domain.Tracking;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Getter
public class ConnectRequest {

    private String identifier;

    private Point startPoint;

    private Point endPoint;

    private Long estimatedArrivalTime;

    public static Tracking toEntity(ConnectRequest request) {
        return Tracking.builder()
                .id(request.getIdentifier())
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .estimatedArrivalTime(request.getEstimatedArrivalTime())
                .startedAt(LocalDateTime.now().plusHours(9))
                .build();
    }
}
