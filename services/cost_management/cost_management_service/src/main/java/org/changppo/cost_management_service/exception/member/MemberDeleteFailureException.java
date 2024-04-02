package org.changppo.cost_management_service.exception.member;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class MemberDeleteFailureException extends MemberBusinessException {
    public MemberDeleteFailureException() {
        super(ExceptionType.MEMBER_DELETE_FAILURE_EXCEPTION);
    }
}