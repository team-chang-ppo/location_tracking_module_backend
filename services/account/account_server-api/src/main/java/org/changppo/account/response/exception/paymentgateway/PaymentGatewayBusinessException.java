package org.changppo.account.response.exception.paymentgateway;

import org.changppo.account.response.exception.common.BusinessException;
import org.changppo.account.response.exception.common.ExceptionType;

public abstract class PaymentGatewayBusinessException extends BusinessException {

    public PaymentGatewayBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }

    public PaymentGatewayBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
