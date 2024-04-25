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

    private double latitude;

    private double longitude;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "Asia/Seoul")
    private String createdAt;

//    private Long remainingArrivalTime; // TODO : 추후 프론트와 협의 후 도입

    private String trackingId;

    @Builder
    public Coordinates(double latitude, double longitude, String createdAt, String trackingId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = createdAt;
        this.trackingId = trackingId;
    }
}
