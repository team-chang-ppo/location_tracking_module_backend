package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayPaymentFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayPaymentFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_PAYMENT_FAILURE_EXCEPTION, cause);
    }
}
