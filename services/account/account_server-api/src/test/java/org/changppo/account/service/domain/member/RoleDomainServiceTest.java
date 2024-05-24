package org.changppo.account.service.domain.member;

import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class RoleDomainServiceTest {

    @InjectMocks
    RoleDomainService roleDomainService;

    @Mock
    RoleRepository roleRepository;

    Member member;
    Role freeRole;
    Role normalRole;

    @BeforeEach
    void beforeEach() {
        freeRole = new Role(RoleType.ROLE_FREE);
        normalRole = new Role(RoleType.ROLE_NORMAL);
        member = buildMember(freeRole);
    }

    @Test
    void hasFreeRoleTest() {
        // given, when
        boolean result = roleDomainService.hasFreeRole(freeRole.getRoleType());

        // then
        assertThat(result).isTrue();
    }

    @Test
    void upgradeRoleTest() {
        // given
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.of(normalRole));

        // when
        roleDomainService.upgradeRole(member);

        // then
        assertThat(member.getRole().getRoleType()).isEqualTo(RoleType.ROLE_NORMAL);
    }

    @Test
    void upgradeRoleExceptionTest() {
        // given
        given(roleRepository.findByRoleType(RoleType.ROLE_NORMAL)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> roleDomainService.upgradeRole(member)).isInstanceOf(RoleNotFoundException.class);
    }

    @Test
    void downgradeRoleTest() {
        // given
        member.changeRole(normalRole);
        given(roleRepository.findByRoleType(RoleType.ROLE_FREE)).willReturn(Optional.of(freeRole));

        // when
        roleDomainService.downgradeRole(member);

        // then
        assertThat(member.getRole().getRoleType()).isEqualTo(RoleType.ROLE_FREE);
    }

    @Test
    void downgradeRoleExceptionTest() {
        // given
        given(roleRepository.findByRoleType(RoleType.ROLE_FREE)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> roleDomainService.downgradeRole(member)).isInstanceOf(RoleNotFoundException.class);
    }
}
