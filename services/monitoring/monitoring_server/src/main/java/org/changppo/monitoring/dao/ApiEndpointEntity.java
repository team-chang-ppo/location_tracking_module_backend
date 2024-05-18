package org.changppo.monitoring.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "api_endpoint")
@NoArgsConstructor
@Getter
@Setter
public class ApiEndpointEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_endpoint_id")
    private Long apiEndpointId;

    @ManyToOne
    @JoinColumn(name = "api_price_group_id")
    private ApiPriceGroupEntity apiPriceGroupEntity;

    @Column(name = "path")
    private String path;

    @Column(name = "method", length = 8)
    private String method;
}
