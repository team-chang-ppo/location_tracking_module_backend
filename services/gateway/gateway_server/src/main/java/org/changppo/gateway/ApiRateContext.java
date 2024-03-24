package org.changppo.gateway;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

/**
 * 각 API Key 별 Rate Limit 정보를 담는 클래스
 */
@Data
@Validated
public class ApiRateContext {

    @Min(1)
    private long replenishRate;

    @Min(0)
    private long burstCapacity = 1;
}
