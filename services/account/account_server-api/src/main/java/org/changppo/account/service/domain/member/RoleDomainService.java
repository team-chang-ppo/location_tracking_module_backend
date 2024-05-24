package org.changppo.account.service.domain.member;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.type.RoleType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class RoleDomainService {

    private final RoleRepository roleRepository;

    public Role getRoleByType(RoleType roleType) {
        return roleRepository.findByRoleType(roleType).orElseThrow(RoleNotFoundException::new);
    }

    public boolean hasFreeRole(RoleType roleType) {
        return roleType == RoleType.ROLE_FREE;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void upgradeRole(Member member) {
        Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
        member.changeRole(normalRole);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void downgradeRole(Member member) {
        Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
        member.changeRole(freeRole);
    }
}
