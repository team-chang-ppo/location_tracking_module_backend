package org.changppo.tracking.api.response;

import lombok.Getter;
import org.changppo.tracking.domain.Coordinates;
import org.springframework.data.geo.Point;

@Getter
public class TrackingResponse {
    private Point locations;

    public TrackingResponse(Coordinates coordinates) {
        this.locations = coordinates.getLocations();
    }
}
