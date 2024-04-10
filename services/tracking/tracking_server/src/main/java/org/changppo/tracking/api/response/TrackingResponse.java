package org.changppo.tracking.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.changppo.tracking.domain.mongodb.Coordinates;
import org.springframework.data.geo.Point;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TrackingResponse {
    private Point locations;

    public TrackingResponse(Coordinates coordinates) {
        this.locations = coordinates.getLocations();
    }
}
