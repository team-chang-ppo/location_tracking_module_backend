package org.changppo.account.response.exception.paymentgateway;

import org.changppo.account.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayApproveFailureException extends PaymentGatewayBusinessException {

    public KakaopayPaymentGatewayApproveFailureException() {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION);
    }

    public KakaopayPaymentGatewayApproveFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION, cause);
    }
}
