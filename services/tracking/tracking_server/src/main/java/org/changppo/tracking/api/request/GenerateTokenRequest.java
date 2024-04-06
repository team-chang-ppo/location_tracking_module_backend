package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.changppo.tracking.api.validation.ValidPoint;
import org.changppo.tracking.domain.Tracking;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GenerateTokenRequest {

    @ValidPoint(message = "시작 지점 좌표가 유효한 값이 아닙니다.")
    private Point startPoint;

    @ValidPoint(message = "도착 지점 좌표가 유효한 값이 아닙니다.")
    private Point endPoint;

    @NotNull(message = "예상 도착 시간은 필수 값 입니다.")
    @Min(value = 1, message = "예상 도착 시간은 1분 이상이어야 합니다.")
    private Long estimatedArrivalTime; // Size 가 필요할까?

    @NotNull(message = "원하는 권한은 필수 값입니다.")
    @Size(min = 1, message = "원하는 권한은 최소 1개 이상이어야 합니다.")
    private List<String> scope;

    @NotNull(message = "토큰 만료 시간은 필수 값입니다.")
    @Min(value = 180, message = "토큰 만료 시간은 180초 이상이어야 합니다.")
    private Long tokenExpiresIn;

    public static Tracking toEntity(String identifier, String apiKeyId, GenerateTokenRequest request) {
        return Tracking.builder()
                .id(identifier)
                .apiKeyId(apiKeyId)
                .startPoint(request.getStartPoint())
                .endPoint(request.getEndPoint())
                .estimatedArrivalTime(request.getEstimatedArrivalTime())
                .startedAt(LocalDateTime.now().plusHours(9))
                .build();
    }
}