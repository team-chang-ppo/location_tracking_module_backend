package org.changppo.tracking.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.changppo.tracking.domain.mongodb.Coordinates;
import org.changppo.tracking.domain.redis.CoordinateRedisEntity;
import org.springframework.data.geo.Point;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    double latitude;
    double longitude;

    public TrackingResponse(Coordinates coordinates) {
        this.latitude = coordinates.getLatitude();
        this.longitude = coordinates.getLongitude();
    }

    public TrackingResponse(CoordinateRedisEntity coordinateRedisEntity) {
        this.latitude = coordinateRedisEntity.latitude();
        this.longitude = coordinateRedisEntity.longitude();
    }

}
