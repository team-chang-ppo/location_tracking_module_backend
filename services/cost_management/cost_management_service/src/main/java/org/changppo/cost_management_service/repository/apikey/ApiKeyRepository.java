package org.changppo.cost_management_service.repository.apikey;

import org.changppo.cost_management_service.dto.apikey.ApiKeyDto;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    @Query("select a from ApiKey a join fetch a.member where a.id = :id")
    Optional<ApiKey> findByIdWithMember(@Param("id") Long id);

    @Query("select a from ApiKey a join fetch a.grade where a.id = :id")
    Optional<ApiKey> findByIdWithGrade(@Param("id") Long id);

    @Query("select new org.changppo.cost_management_service.dto.apikey.ApiKeyDto(a.id, a.value, a.grade.gradeType, a.paymentFailureBannedAt, a.cardDeletionBannedAt, a.createdAt) " +
            "from ApiKey a where a.member.id = :memberId and a.id >= :firstApiKeyId " +
            "order by a.id asc")
    Slice<ApiKeyDto> findAllByMemberIdOrderByAsc(@Param("memberId") Long memberId, @Param("firstApiKeyId") Long firstApiKeyId, Pageable pageable);

    void deleteAllByMemberId(Long memberId);

    Optional<ApiKey> findByValue(String value);

    long countByMemberId(Long memberId);

    List<ApiKey> findAllByMemberId(Long memberId);

    @Modifying
    @Query("UPDATE ApiKey a SET a.cardDeletionBannedAt = CURRENT_TIMESTAMP WHERE a.member.id = :memberId AND a.grade.gradeType != 'GRADE_FREE'")
    void banForCardDeletionByMemberId(@Param("memberId")Long memberId);

    @Modifying
    @Query("UPDATE ApiKey a SET a.cardDeletionBannedAt = NULL WHERE a.member.id = :memberId AND a.grade.gradeType != 'GRADE_FREE'")
    void unbanForCardDeletionByMemberId(@Param("memberId")Long memberId);
}
