package org.changppo.cost_management_service.exception.paymentgateway;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class KakaopayPaymentGatewayFailFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayFailFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_FAIL_FAILURE_EXCEPTION, cause);
    }
}