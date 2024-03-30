package org.changppo.tracking.api.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.tracking.api.validation.ValidPoint;
import org.changppo.tracking.domain.Coordinates;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {

    @ValidPoint(message = "현재 좌표가 유효한 값이 아닙니다.")
    private Point locations;

//    private Long remainingArrivalTime; // TODO : 추후 프론트와 협의 후 도입

    public static Coordinates toCoordinatesEntity(TrackingRequest request, String trackingId) {
        return Coordinates.builder()
                .locations(request.getLocations())
                .createdAt(LocalDateTime.now().plusHours(9))
                .trackingId(trackingId)
                .build();
    }
}
