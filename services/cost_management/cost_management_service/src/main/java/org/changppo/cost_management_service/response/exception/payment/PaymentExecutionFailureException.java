package org.changppo.cost_management_service.response.exception.payment;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class PaymentExecutionFailureException extends PaymentBusinessException {
    public PaymentExecutionFailureException() {
        super(ExceptionType.PAYMENT_EXECUTION_FAILURE_EXCEPTION);
    }

    public PaymentExecutionFailureException(Throwable cause) {
        super(ExceptionType.PAYMENT_EXECUTION_FAILURE_EXCEPTION, cause);
    }
}
