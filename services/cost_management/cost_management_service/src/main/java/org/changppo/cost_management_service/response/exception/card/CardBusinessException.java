package org.changppo.cost_management_service.response.exception.card;

import org.changppo.cost_management_service.response.exception.common.BusinessException;
import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public abstract class CardBusinessException extends BusinessException {

    public CardBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }

    public CardBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
