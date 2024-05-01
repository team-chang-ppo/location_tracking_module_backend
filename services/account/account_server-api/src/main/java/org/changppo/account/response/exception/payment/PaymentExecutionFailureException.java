package org.changppo.account.response.exception.payment;

import org.changppo.account.response.exception.common.ExceptionType;

public class PaymentExecutionFailureException extends PaymentBusinessException {
    public PaymentExecutionFailureException() {
        super(ExceptionType.PAYMENT_EXECUTION_FAILURE_EXCEPTION);
    }

    public PaymentExecutionFailureException(Throwable cause) {
        super(ExceptionType.PAYMENT_EXECUTION_FAILURE_EXCEPTION, cause);
    }
}
