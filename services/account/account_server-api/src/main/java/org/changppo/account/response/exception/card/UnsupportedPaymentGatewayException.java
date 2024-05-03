package org.changppo.account.response.exception.card;

import org.changppo.account.response.exception.common.ExceptionType;

public class UnsupportedPaymentGatewayException extends CardBusinessException {
    public UnsupportedPaymentGatewayException() {
        super(ExceptionType.UNSUPPORTED_PAYMENT_GATEWAY_EXCEPTION);
    }
}
