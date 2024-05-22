package org.changppo.monitoring.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.changppo.monioring.domain.HourlyApiUsageEvent;

import java.time.LocalDate;

@Entity
@Table(name = "hourly_api_usage")
@NoArgsConstructor
@Getter
@Setter
public class HourlyApiUsageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_usage_id")
    private Long hourlyApiUsageId;

    // FK , many to one
    @Column(name = "api_endpoint_id")
    private Long apiEndpointId;

    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "api_key_id")
    private Long apiKeyId;

    @Column(name = "date")
    private LocalDate date;

    @Column(name = "hour")
    private Integer hour;

    @Column(name = "count")
    private Long count;

    public static HourlyApiUsageEntity from(HourlyApiUsageEvent event) {
        HourlyApiUsageEntity entity = new HourlyApiUsageEntity();
        entity.setApiEndpointId(event.getApiEndpointId());
        entity.setMemberId(event.getMemberId());
        entity.setApiKeyId(event.getApiKeyId());
        entity.setDate(event.getDate());
        entity.setHour(event.getWindow().getStart().getHour());
        entity.setCount(event.getCount());
        return entity;
    }
}
