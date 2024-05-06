package org.changppo.monitoring.dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Document(collection = "api_price")
@TypeAlias("api_price")
public class ApiPriceDocument {
    @Id
    private String routeId;
    @Field("price")
    private Long price;
}
