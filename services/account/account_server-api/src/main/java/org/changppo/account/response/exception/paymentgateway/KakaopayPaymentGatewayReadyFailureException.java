package org.changppo.account.response.exception.paymentgateway;

import org.changppo.account.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayReadyFailureException extends PaymentGatewayBusinessException {

    public KakaopayPaymentGatewayReadyFailureException() {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION);
    }

    public KakaopayPaymentGatewayReadyFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION, cause);
    }
}
