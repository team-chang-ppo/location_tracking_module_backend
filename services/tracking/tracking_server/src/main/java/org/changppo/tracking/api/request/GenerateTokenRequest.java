package org.changppo.tracking.api.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

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

}