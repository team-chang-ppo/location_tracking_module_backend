package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.changppo.tracking.domain.mongodb.Tracking;
import org.changppo.tracking.domain.TrackingContext;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@AllArgsConstructor
public class StartTrackingRequest {

    @Min(value = -90, message = "위도는 -90 이상이여야 합니다.")
    @Max(value = 90, message = "위도는 90 이하여야 합니다.")
    private double startLatitude;

    @Min(value = -180, message = "경도는 -180 이상이여야 합니다.")
    @Max(value = 180, message = "경도는 180 이하여야 합니다.")
    private double startLongitude;

    @Min(value = -90, message = "위도는 -90 이상이여야 합니다.")
    @Max(value = 90, message = "위도는 90 이하여야 합니다.")
    private double endLatitude;

    @Min(value = -180, message = "경도는 -180 이상이여야 합니다.")
    @Max(value = 180, message = "경도는 180 이하여야 합니다.")
    private double endLongitude;

    @NotNull(message = "예상 도착 시간은 필수 값 입니다.")
    @Min(value = 1, message = "예상 도착 시간은 1분 이상이어야 합니다.")
    private Long estimatedArrivalTime; // Size 가 필요할까?

    public static Tracking toEntity(TrackingContext context, StartTrackingRequest request) {
        return Tracking.builder()
                .id(context.trackingId())
                .apiKeyId(context.apiKeyId())
                .scope(context.scopes())
                .startLatitude(request.getStartLatitude())
                .startLongitude(request.getStartLongitude())
                .endLatitude(request.getEndLatitude())
                .endLongitude(request.getEndLongitude())
                .estimatedArrivalTime(request.getEstimatedArrivalTime())
                .startedAt(ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")))
                .build();
    }
}
