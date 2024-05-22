package org.changppo.monitoring.dao;


import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface ApiEndpointRepository extends Repository<ApiEndpointEntity, Long> {
    // select id only from api_endpoint where id in []
    List<ApiEndpointIdFragment> findAllByApiEndpointIdIn(List<Long> apiEndpointIds);

}
