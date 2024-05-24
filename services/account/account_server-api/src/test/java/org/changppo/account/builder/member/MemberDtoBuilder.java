package org.changppo.account.builder.member;

import org.changppo.account.entity.member.Role;
import org.changppo.account.service.dto.member.MemberDto;

import java.time.LocalDateTime;

public class MemberDtoBuilder {
    public static MemberDto buildMemberDto(Role role) {
        return new MemberDto(1L, "testName", "testUsername", "testProfileImage", role.getRoleType(), LocalDateTime.now(), LocalDateTime.now());
    }
}
