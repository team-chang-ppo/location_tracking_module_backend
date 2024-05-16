package org.changppo.account.repository.member;

import org.changppo.account.entity.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {  //TODO. 복잡한 쿼리는 Querydsl로 변경
    Optional<Member> findByName(String name);

    @Query("select m from Member m join fetch m.memberRoles where m.name = :name")
    Optional<Member> findByNameWithRoles(@Param("name") String name);

    @Query("select m from Member m join fetch m.memberRoles where m.id = :id")
    Optional<Member> findByIdWithRoles(@Param("id") Long id);
}
