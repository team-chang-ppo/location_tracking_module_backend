package org.changppo.account.response.exception.paymentgateway;

import org.changppo.account.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayFailException extends PaymentGatewayBusinessException {

    public KakaopayPaymentGatewayFailException() {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION);
    }

    public KakaopayPaymentGatewayFailException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION, cause);
    }
}
