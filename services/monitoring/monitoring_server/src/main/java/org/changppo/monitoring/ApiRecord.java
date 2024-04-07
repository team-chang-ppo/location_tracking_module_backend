package org.changppo.monitoring;

import java.time.Instant;
import java.util.Map;

public record ApiRecord(
        Long apiKeyId,
        String routeId,
        Instant timestamp,
        Map<String, String> details
) {
}
