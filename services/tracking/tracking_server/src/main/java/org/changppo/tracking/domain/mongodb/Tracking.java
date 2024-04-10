package org.changppo.tracking.domain.mongodb;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(collection = "tracking")
public class Tracking {
    @Id
    private String id;

    private String apiKeyId;

    private List<String> scope;

    private Point startPoint;

    private Point endPoint;

    private Long estimatedArrivalTime; // 분 단위

    private String startedAt;

    private String endedAt;

    @ReadOnlyProperty
    @DocumentReference(lookup="{'trackingId':?#{#self._id} }")
    private List<Coordinates> coordinatesList;

    @Builder
    public Tracking(String id, String apiKeyId, List<String> scope, Point startPoint, Point endPoint, Long estimatedArrivalTime, String startedAt) {
        this.id = id;
        this.apiKeyId = apiKeyId;
        this.scope = scope;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.startedAt = startedAt;
    }


    public void updateEndedAt() {
        this.endedAt = ZonedDateTime.ofInstant(Instant.now(), ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'"));
    }
}