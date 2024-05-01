package org.changppo.account.response.exception.payment;

import org.changppo.account.response.exception.common.BusinessException;
import org.changppo.account.response.exception.common.ExceptionType;

public abstract class PaymentBusinessException extends BusinessException {

    public PaymentBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public PaymentBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
