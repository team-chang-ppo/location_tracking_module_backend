package org.changppo.cost_management_service.response.exception.member;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class RoleNotFoundException extends MemberBusinessException {
    public RoleNotFoundException() {
        super(ExceptionType.ROLE_NOT_FOUND_EXCEPTION);
    }
}
