package org.changppo.gateway.metering;

import java.time.Instant;
import java.util.Map;

public record ApiRecord(
        Long apiKeyId,
        String routeId,
        Instant timestamp,
        Map<String, String> details
) {

    public ApiRecord(Long apiKeyId, String routeId, Instant timestamp, Map<String, String> details) {
        this.apiKeyId = apiKeyId;
        this.routeId = routeId;
        this.timestamp = timestamp;
        this.details = details;
    }

    public ApiRecord(Long apiKeyId, String routeId, Instant timestamp) {
        this(apiKeyId, routeId, timestamp, Map.of());
    }

    public String toJson() {
        return String.format("{\"apiKeyId\":%d,\"routeId\":\"%s\",\"timestamp\":\"%s\",\"details\":%s}",
                apiKeyId, routeId, timestamp, details);
    }
}
