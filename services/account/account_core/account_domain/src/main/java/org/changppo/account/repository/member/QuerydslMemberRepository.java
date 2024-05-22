package org.changppo.account.repository.member;

import org.changppo.account.entity.member.Member;
import org.changppo.account.service.dto.member.MemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface QuerydslMemberRepository {
    Optional<Member> findByNameWithRoles(String name);
    Optional<Member> findByIdWithRoles(Long id);
    Page<MemberDto> findAllDtos(Pageable pageable);
    Optional<MemberDto> findDtoById(Long id);
}
