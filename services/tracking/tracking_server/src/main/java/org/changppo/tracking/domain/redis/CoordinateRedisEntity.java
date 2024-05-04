package org.changppo.tracking.domain.redis;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.changppo.tracking.domain.mongodb.Coordinates;
import org.changppo.tracking.domain.mongodb.Tracking;

import java.time.LocalDateTime;

public record CoordinateRedisEntity(
        double latitude,
        double longitude,
        String trackingId,
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime createdAt

) {
    public CoordinateRedisEntity(Coordinates coordinates) {
        this(
                coordinates.getLatitude(),
                coordinates.getLongitude(),
                coordinates.getTrackingId(),
                coordinates.getCreatedAt()
        );
    }

    public static Coordinates toCoordinates(CoordinateRedisEntity redisEntity) {
        return Coordinates.builder()
                .latitude(redisEntity.latitude)
                .longitude(redisEntity.longitude)
                .trackingId(redisEntity.trackingId)
                .createdAt(redisEntity.createdAt)
                .build();
    }
}
