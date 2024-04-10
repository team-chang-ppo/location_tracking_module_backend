package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.changppo.tracking.api.validation.ValidPoint;
import org.changppo.tracking.domain.Tracking;
import org.changppo.tracking.domain.TrackingContext;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StartTrackingRequest {
    @ValidPoint(message = "시작 지점 좌표가 유효한 값이 아닙니다.")
    private Point startPoint;

    @ValidPoint(message = "도착 지점 좌표가 유효한 값이 아닙니다.")
    private Point endPoint;

    @NotNull(message = "예상 도착 시간은 필수 값 입니다.")
    @Min(value = 1, message = "예상 도착 시간은 1분 이상이어야 합니다.")
    private Long estimatedArrivalTime; // Size 가 필요할까?

    public static Tracking toEntity(TrackingContext context, StartTrackingRequest request) {
        return Tracking.builder()
                .id(context.trackingId())
                .apiKeyId(context.apiKeyId())
                .scope(context.scopes())
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .estimatedArrivalTime(request.getEstimatedArrivalTime())
                .startedAt(LocalDateTime.now().plusHours(9))
                .build();
    }
}
