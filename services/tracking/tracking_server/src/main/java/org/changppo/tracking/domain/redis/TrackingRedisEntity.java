package org.changppo.tracking.domain.redis;

import org.changppo.tracking.domain.mongodb.Tracking;

public record TrackingRedisEntity(
        String trackingId,
        String apiKeyId,
        String endedAt
) {
    public TrackingRedisEntity(Tracking tracking) {
        this(
                tracking.getId(),
                tracking.getApiKeyId(),
                tracking.getEndedAt()
        );
    }
}
