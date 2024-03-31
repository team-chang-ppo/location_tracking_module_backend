package org.changppo.cost_management_service.repository.apikey;

import org.changppo.cost_management_service.dto.apikey.ApiKeyDto;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    @Query("select a from ApiKey a join fetch a.member where a.id = :id")
    Optional<ApiKey> findByIdWithMember(@Param("id") Long id);

    @Query("select a from ApiKey a join fetch a.grade where a.id = :id")
    Optional<ApiKey> findByIdWithGrade(@Param("id") Long id);

    @Query("select new org.changppo.cost_management_service.dto.apikey.ApiKeyDto(a.id, a.value, a.grade.gradeType, a.bannedAt, a.createdAt) " +
            "from ApiKey a where a.member.id = :id and a.id >= :firstApiKeyId " +
            "order by a.id asc")
    Slice<ApiKeyDto> findAllByMemberIdOrderByAsc(@Param("id") Long id, @Param("firstApiKeyId") Long firstApiKeyId, Pageable pageable);

    void deleteAllByMemberId(Long id);

    Optional<ApiKey> findByValue(String value);

    long countByMemberId(Long id);
}
