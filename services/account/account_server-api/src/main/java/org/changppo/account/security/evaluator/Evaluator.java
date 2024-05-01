package org.changppo.account.security.evaluator;

import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.type.RoleType;

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