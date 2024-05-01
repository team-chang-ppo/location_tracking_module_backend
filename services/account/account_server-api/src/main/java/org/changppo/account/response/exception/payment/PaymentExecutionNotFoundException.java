package org.changppo.account.response.exception.payment;

import org.changppo.account.response.exception.common.ExceptionType;

public class PaymentExecutionNotFoundException extends PaymentBusinessException {
    public PaymentExecutionNotFoundException() {
        super(ExceptionType.PAYMENT_EXECUTION_NOT_FOUND_EXCEPTION);
    }
}
