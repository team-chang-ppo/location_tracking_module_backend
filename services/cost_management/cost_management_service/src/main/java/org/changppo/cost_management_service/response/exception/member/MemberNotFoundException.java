package org.changppo.cost_management_service.response.exception.member;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class MemberNotFoundException extends MemberBusinessException {

    public MemberNotFoundException() {
        super(ExceptionType.MEMBER_NOT_FOUND_EXCEPTION);
    }
}
