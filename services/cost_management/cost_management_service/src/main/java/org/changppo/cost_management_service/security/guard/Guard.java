package org.changppo.cost_management_service.security.guard;


import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.security.PrincipalHandler;

import java.util.List;

public abstract class Guard {
    public final boolean check(Long id) {
        return hasRole(getRoleTypes()) || isResourceOwner(id);
    }

    abstract protected List<RoleType> getRoleTypes();
    abstract protected boolean isResourceOwner(Long id);

    private boolean hasRole(List<RoleType> roleTypes) {
        return PrincipalHandler.extractMemberRoles().containsAll(roleTypes);
    }
}