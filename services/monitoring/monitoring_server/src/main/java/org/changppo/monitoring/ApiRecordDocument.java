package org.changppo.monitoring;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@Document(collection = "api_record")
@TypeAlias("api_record_document")
public class ApiRecordDocument {

    @Id
    private String id;
    @Field("api_key_id")
    private Long apiKeyId;
    @Field("route_id")
    private String routeId;
    @Field("timestamp")
    private Instant timestamp;
    @Field("details")
    private Map<String, String> details;

    public ApiRecordDocument(Long apiKeyId, String routeId, Instant timestamp, Map<String, String> details) {
        this.apiKeyId = apiKeyId;
        this.routeId = routeId;
        this.timestamp = timestamp;
        this.details = details;
    }
}
