package org.changppo.tracking.domain;

import java.util.List;

public record TrackingContext(
        String trackingId,
        Long apiKeyId,
        List<String> scopes
) {
}