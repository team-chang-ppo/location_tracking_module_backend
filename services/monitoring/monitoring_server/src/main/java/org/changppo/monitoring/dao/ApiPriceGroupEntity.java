package org.changppo.monitoring.dao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "api_price_group")
@Getter
@Setter
@NoArgsConstructor
public class ApiPriceGroupEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long apiPriceGroupId;

    @Column(name = "group_name")
    private String groupName;

    @OneToMany(mappedBy = "apiPriceGroupEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApiPriceEntity> apiPriceEntities = new ArrayList<>();

    @OneToMany(mappedBy = "apiPriceGroupEntity", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ApiEndpointEntity> apiEndpointEntities = new ArrayList<>();
}
