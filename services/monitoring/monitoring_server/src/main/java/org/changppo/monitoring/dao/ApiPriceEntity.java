package org.changppo.monitoring.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "api_price")
@NoArgsConstructor
@Getter
@Setter
public class ApiPriceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_price_id")
    private Long apiPriceId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "cost_won")
    private Long costWon;

    @Column(name = "priority")
    private Integer priority;

    @ManyToOne
    @JoinColumn(name = "api_price_group_id")
    private ApiPriceGroupEntity apiPriceGroupEntity;
}
