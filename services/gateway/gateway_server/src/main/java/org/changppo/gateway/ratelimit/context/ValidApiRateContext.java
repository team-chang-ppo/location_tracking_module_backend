package org.changppo.gateway.ratelimit.context;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

/**
 * 각 API Key 별 Rate Limit 정보를 담는 클래스
 */
@Builder
@Validated
public record ValidApiRateContext(
        @NotBlank
        String key,
        @Min(1)
        long replenishRate,
        @Min(0)
        long burstCapacity

) implements ApiRateContext {
}
