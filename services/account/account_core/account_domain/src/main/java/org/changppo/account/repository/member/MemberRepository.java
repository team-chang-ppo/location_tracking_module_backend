package org.changppo.account.repository.member;

import org.changppo.account.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByName(String name);

    @Query("select m from Member m join fetch m.memberRoles where m.name = :name")
    Optional<Member> findByNameWithRoles(@Param("name") String name);

    @Query(value = "select * from member where name = :name", nativeQuery = true)
    Optional<Member> findByNameIgnoringDeleted(@Param("name") String name);

    @Query("select m from Member m join fetch m.memberRoles where m.id = :id")
    Optional<Member> findByIdWithRoles(@Param("id") Long id);

    @Query("select m from Member m where m.paymentFailureBannedAt is null and m.deletionRequestedAt is null")
    Page<Member> findMembersForAutomaticPayment(Pageable pageable);

    @Query("select m from Member m where m.deletionRequestedAt is not null and m.deletionRequestedAt <= :time and m.paymentFailureBannedAt is null")
    Page<Member> findMembersForDeletion(@Param("time") LocalDateTime time, Pageable pageable);
}
