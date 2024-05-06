package org.changppo.monitoring.dao;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface ApiPriceRepository extends MongoRepository<ApiPriceDocument, String> {
    List<ApiPriceDocument> findByRouteIdIn(Set<String> routeIds);
}
