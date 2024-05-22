package org.changppo.monitoring.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;

@Entity
@Immutable
@NoArgsConstructor
@Getter
@Table(name = "hourly_api_usage_cost")
@IdClass(CostViewId.class)
public class HourlyApiUsageCostView {
    @Id
    @Column(name = "member_id")
    private Long memberId;

    @Id
    @Column(name = "api_key_id")
    private Long apiKeyId;

    @Id
    @Column(name = "date")
    private LocalDate date;

    @Id
    @Column(name = "hour")
    private Integer hour;

    @Id
    @Column(name = "api_endpoint_id")
    private Long apiEndpointId;

    @Id
    @Column(name = "api_price_group_id")
    private Long apiPriceGroupId;

    @Column(name = "cost_per_request")
    private Long costPerRequest;

    @Column(name = "request_count_per_hour")
    private Long requestCount;

    @Column(name = "hourly_cost")
    private Long hourlyCost;

}
