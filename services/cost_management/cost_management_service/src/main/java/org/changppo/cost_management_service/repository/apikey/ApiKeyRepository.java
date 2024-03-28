package org.changppo.cost_management_service.repository.apikey;

import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

}
