package org.changppo.cost_management_service.security.guard;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.security.PrincipalHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MemberGuard extends Guard {
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isResourceOwner(Long id) {
        return id.equals(PrincipalHandler.extractId());
    }
}