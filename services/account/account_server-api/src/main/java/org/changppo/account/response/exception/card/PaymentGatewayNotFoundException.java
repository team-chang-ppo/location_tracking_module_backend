package org.changppo.account.response.exception.card;

import org.changppo.account.response.exception.common.ExceptionType;

public class PaymentGatewayNotFoundException extends CardBusinessException {
    public PaymentGatewayNotFoundException() {
        super(ExceptionType.PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION);
    }
}
