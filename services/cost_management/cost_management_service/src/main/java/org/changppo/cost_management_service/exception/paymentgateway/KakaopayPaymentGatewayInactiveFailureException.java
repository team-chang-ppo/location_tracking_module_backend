package org.changppo.cost_management_service.exception.paymentgateway;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class KakaopayPaymentGatewayInactiveFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayInactiveFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_INACTIVE_FAILURE_EXCEPTION, cause);
    }
}