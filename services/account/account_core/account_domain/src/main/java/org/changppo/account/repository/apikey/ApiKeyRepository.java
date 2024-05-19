package org.changppo.account.repository.apikey;

import org.changppo.account.entity.apikey.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> , QuerydslApiKeyRepository {
    Optional<ApiKey> findByValue(String value);
    long countByMemberId(Long memberId);
}
