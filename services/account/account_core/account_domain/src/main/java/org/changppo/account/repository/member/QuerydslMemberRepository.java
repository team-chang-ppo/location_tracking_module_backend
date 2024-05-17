package org.changppo.account.repository.member;

import org.changppo.account.entity.member.Member;

import java.util.Optional;

public interface QuerydslMemberRepository {
    Optional<Member> findByNameWithRoles(String name);
    Optional<Member> findByIdWithRoles(Long id);
}
