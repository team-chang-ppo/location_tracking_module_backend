package org.changppo.cost_management_service.response.exception.payment;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class PaymentExecutionNotFoundException extends PaymentBusinessException {
    public PaymentExecutionNotFoundException() {
        super(ExceptionType.PAYMENT_EXECUTION_NOT_FOUND_EXCEPTION);
    }
}
