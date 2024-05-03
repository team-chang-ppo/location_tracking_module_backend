package org.changppo.account.response.exception.card;

import org.changppo.account.response.exception.common.BusinessException;
import org.changppo.account.response.exception.common.ExceptionType;

public abstract class CardBusinessException extends BusinessException {

    public CardBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }

    public CardBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
