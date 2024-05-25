package org.changppo.account.builder.member;


import org.changppo.account.entity.member.Role;
import org.changppo.account.type.RoleType;

public class RoleBuilder {
    public static Role buildRole(RoleType roleType) {
        return new Role(roleType);
    }
}
