package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.changppo.tracking.api.validation.ValidPoint;
import org.changppo.tracking.domain.Tracking;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class GenerateTokenRequest {

    @Setter
    @NotBlank(message = "식별자는 빈값 일 수 없습니다.")
    private String identifier;

    @NotNull(message = "원하는 권한은 필수 값입니다.")
    @Size(min = 1, message = "원하는 권한은 최소 1개 이상이어야 합니다.")
    private List<String> scope;

    @NotNull(message = "토큰 만료 시간은 필수 값입니다.")
    @Min(value = 180, message = "토큰 만료 시간은 180초 이상이어야 합니다.")
    private Long tokenExpiresIn;

}