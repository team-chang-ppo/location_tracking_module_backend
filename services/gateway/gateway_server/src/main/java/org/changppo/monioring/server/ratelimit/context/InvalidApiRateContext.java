package org.changppo.monioring.server.ratelimit.context;

import jakarta.validation.constraints.NotBlank;

public record InvalidApiRateContext(
        @NotBlank
        String key,
        String[] reasons

) implements ApiRateContext{
}
