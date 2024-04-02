package org.changppo.cost_management_service.exception.member;

import org.changppo.cost_management_service.exception.common.BusinessException;
import org.changppo.cost_management_service.exception.common.ExceptionType;

public class MemberBusinessException extends BusinessException {

    public MemberBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
