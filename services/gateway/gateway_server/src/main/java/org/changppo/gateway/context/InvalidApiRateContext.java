package org.changppo.gateway.context;

import jakarta.validation.constraints.NotBlank;

public record InvalidApiRateContext(
        @NotBlank
        String key,
        String[] reasons

) implements ApiRateContext{
}
