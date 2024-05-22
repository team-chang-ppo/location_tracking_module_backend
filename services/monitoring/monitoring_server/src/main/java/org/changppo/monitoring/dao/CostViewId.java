package org.changppo.monitoring.dao;

import jakarta.persistence.Column;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class CostViewId implements Serializable {
    @Serial
    private static final long serialVersionUID = 1122593031541339037L;

    @EqualsAndHashCode.Include
    @Column(name = "member_id")
    private Long memberId;

    @EqualsAndHashCode.Include
    @Column(name = "api_key_id")
    private Long apiKeyId;

    @EqualsAndHashCode.Include
    @Column(name = "date")
    private LocalDate date;

    @EqualsAndHashCode.Include
    @Column(name = "hour")
    private Integer hour;

    @EqualsAndHashCode.Include
    @Column(name = "api_endpoint_id")
    private Long apiEndpointId;

    @EqualsAndHashCode.Include
    @Column(name = "api_price_group_id")
    private Long apiPriceGroupId;

}
