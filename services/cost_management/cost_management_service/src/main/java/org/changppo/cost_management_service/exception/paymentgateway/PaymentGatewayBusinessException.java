package org.changppo.cost_management_service.exception.paymentgateway;

import org.changppo.cost_management_service.exception.common.BusinessException;
import org.changppo.cost_management_service.exception.common.ExceptionType;

public class PaymentGatewayBusinessException extends BusinessException {

    public PaymentGatewayBusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
