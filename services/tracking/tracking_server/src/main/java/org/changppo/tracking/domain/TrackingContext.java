package org.changppo.tracking.domain;

import java.util.List;

public record TrackingContext(
        String trackingId,
        String apiKeyId,
        List<String> scopes
) {
}