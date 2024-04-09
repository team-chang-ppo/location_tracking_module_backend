package org.changppo.gateway.metering;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.changppo.gateway.apikey.ApiKey;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

public record ApiMeteringEvent(
        String eventId,
        Long memberId,
        Long apiKeyId,
        String routeId
) {

    public static ApiMeteringEvent createFromApiKey(ApiKey apiKey, String routeId) {
        String eventId = UUID.randomUUID().toString();
        Long apiKeyId = apiKey.id();
        Long memberId = apiKey.memberId();
        return new ApiMeteringEvent(eventId, memberId, apiKeyId, routeId);
    }
}
