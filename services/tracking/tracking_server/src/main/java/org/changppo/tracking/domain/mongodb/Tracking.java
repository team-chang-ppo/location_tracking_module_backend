package org.changppo.tracking.domain.mongodb;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Document(collection = "tracking")
public class Tracking {
    @Id
    private String id;

    private String apiKeyId;

    private List<String> scope;

    private double startLatitude;

    private double startLongitude;

    private double endLatitude;

    private double endLongitude;

    private Long estimatedArrivalTime; // 분 단위

    private LocalDateTime startedAt;

    private LocalDateTime endedAt;

    @ReadOnlyProperty
    @DocumentReference(lookup="{'trackingId':?#{#self._id} }")
    private List<Coordinates> coordinatesList;

    @Builder
    public Tracking(String id, String apiKeyId, List<String> scope, double startLatitude, double startLongitude, double endLatitude, double endLongitude, Long estimatedArrivalTime, LocalDateTime startedAt) {
        this.id = id;
        this.apiKeyId = apiKeyId;
        this.scope = scope;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.estimatedArrivalTime = estimatedArrivalTime;
        this.startedAt = startedAt;
    }

    public void updateEndedAt() {
        this.endedAt = LocalDateTime.now();
    }
}