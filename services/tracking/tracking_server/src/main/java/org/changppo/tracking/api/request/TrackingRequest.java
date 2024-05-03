package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.tracking.domain.mongodb.Coordinates;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {

    @Min(value = -90, message = "위도는 -90 이상이여야 합니다.")
    @Max(value = 90, message = "위도는 90 이하여야 합니다.")
    private double latitude;

    @Min(value = -180, message = "경도는 -180 이상이여야 합니다.")
    @Max(value = 180, message = "경도는 180 이하여야 합니다.")
    private double longitude;

//    private Long remainingArrivalTime; // TODO : 추후 프론트와 협의 후 도입

    public static Coordinates toCoordinatesEntity(TrackingRequest request, String trackingId) {
        return Coordinates.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .createdAt(LocalDateTime.now())
                .trackingId(trackingId)
                .build();
    }
}
