package org.changppo.tracking.api.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.changppo.tracking.api.validation.ValidPoint;
import org.changppo.tracking.domain.Tracking;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ConnectRequest {

    @NotBlank(message = "식별자는 빈값 일 수 없습니다.")
    private String identifier;

    @ValidPoint(message = "시작 지점 좌표가 유효한 값이 아닙니다.")
    private Point startPoint;

    @ValidPoint(message = "도착 지점 좌표가 유효한 값이 아닙니다.")
    private Point endPoint;

    @NotNull(message = "예상 도착 시간은 필수 값 입니다.")
    private Long estimatedArrivalTime; // Size 가 필요할까?

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
