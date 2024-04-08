package org.changppo.monitoring;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Document(collection = "api_record")
@TypeAlias("api_metering_event")
public class ApiMeteringEventDocument {

    @Id
    private String id;
    @Field("member_id")
    private Long memberId;
    @Field("api_key_id")
    private Long apiKeyId;
    @Field("route_id")
    private String routeId;
    @Field("timestamp")
    private Instant timestamp;

    @Builder
    public ApiMeteringEventDocument(String id, Long apiKeyId, Long memberId, String routeId, Instant timestamp) {
        Assert.notNull(id, "id must not be null");
        Assert.notNull(apiKeyId, "apiKeyId must not be null");
        Assert.notNull(memberId, "memberId must not be null");
        Assert.notNull(routeId, "routeId must not be null");
        Assert.notNull(timestamp, "timestamp must not be null");
        this.id = id;
        this.apiKeyId = apiKeyId;
        this.memberId = memberId;
        this.routeId = routeId;
        this.timestamp = timestamp;
    }

    public static ApiMeteringEventDocument createFromApiMeteringEvent(ApiMeteringEvent event, Instant timestamp) {
        Assert.notNull(event, "event must not be null");
        Assert.notNull(timestamp, "timestamp must not be null");
        return ApiMeteringEventDocument.builder()
                .id(event.eventId())
                .apiKeyId(event.apiKeyId())
                .memberId(event.memberId())
                .routeId(event.routeId())
                .timestamp(timestamp)
                .build();
    }
}
