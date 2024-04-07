package org.changppo.monitoring;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

public record ApiMeteringEvent(
        String eventId,
        Long memberId,
        Long apiKeyId,
        String routeId,
        Instant timestamp,
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> details
) {
}
