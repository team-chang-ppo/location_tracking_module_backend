package org.changppo.tracking.domain.mongodb;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Document(collection = "coordinates")
public class Coordinates {
    @Id
    private String id;

    private Point locations;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Asia/Seoul")
    private String createdAt;

//    private Long remainingArrivalTime; // TODO : 추후 프론트와 협의 후 도입

    private String trackingId;

    @Builder
    public Coordinates(Point locations, String createdAt, String trackingId) {
        this.locations = locations;
        this.createdAt = createdAt;
        this.trackingId = trackingId;
    }
}
