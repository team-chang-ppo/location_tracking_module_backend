package org.changppo.cost_management_service.response.exception.payment;

import org.changppo.cost_management_service.response.exception.common.BusinessException;
import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public abstract class PaymentBusinessException extends BusinessException {

    public PaymentBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public PaymentBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
