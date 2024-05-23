package org.changppo.tracking.domain.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.changppo.tracking.domain.mongodb.Tracking;

import java.time.LocalDateTime;

public record TrackingRedisEntity(
        String trackingId,
        Long apiKeyId,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime endedAt
) {
    public TrackingRedisEntity(Tracking tracking) {
        this(
                tracking.getId(),
                tracking.getApiKeyId(),
                tracking.getEndedAt()
        );
    }
}
