package org.changppo.cost_management_service.response.exception.member;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class UpdateAuthenticationFailureException extends MemberBusinessException {
    public UpdateAuthenticationFailureException() {
        super(ExceptionType.UPDATE_AUTHENTICATION_FAILURE_EXCEPTION);
    }
}