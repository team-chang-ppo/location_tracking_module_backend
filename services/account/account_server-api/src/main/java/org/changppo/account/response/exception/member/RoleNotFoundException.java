package org.changppo.account.response.exception.member;

import org.changppo.account.response.exception.common.ExceptionType;

public class RoleNotFoundException extends MemberBusinessException {
    public RoleNotFoundException() {
        super(ExceptionType.ROLE_NOT_FOUND_EXCEPTION);
    }
}
