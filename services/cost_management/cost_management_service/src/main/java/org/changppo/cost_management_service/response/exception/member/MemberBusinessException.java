package org.changppo.cost_management_service.response.exception.member;

import org.changppo.cost_management_service.response.exception.common.BusinessException;
import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public abstract class MemberBusinessException extends BusinessException {

    public MemberBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public MemberBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
