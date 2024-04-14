package org.changppo.cost_management_service.repository.member;

import org.changppo.cost_management_service.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);

    @Query("select m from Member m join fetch m.memberRoles where m.name = :name")
    Optional<Member> findByNameWithRoles(@Param("name") String name);

    @Query(value = "select * from member where name = :name", nativeQuery = true)
    Optional<Member> findByNameIgnoringDeleted(@Param("name") String name);

    @Query("select m from Member m join fetch m.memberRoles where m.id = :id")
    Optional<Member> findByIdWithRoles(@Param("id") Long id);

    Page<Member> findByPaymentFailureBannedAtIsNull(Pageable pageable);
}
