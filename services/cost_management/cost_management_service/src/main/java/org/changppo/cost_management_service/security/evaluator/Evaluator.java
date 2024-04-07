package org.changppo.cost_management_service.security.evaluator;

import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.security.PrincipalHandler;
import java.util.List;

public abstract class Evaluator {
    public final boolean check(Long id) {
        return hasRole(getRoleTypes()) || isEligible(id);
    }
    abstract protected List<RoleType> getRoleTypes();
    abstract protected boolean isEligible(Long id);

    private boolean hasRole(List<RoleType> roleTypes) {
        return PrincipalHandler.extractMemberRoles().containsAll(roleTypes);
    }
}