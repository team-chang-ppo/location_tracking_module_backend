package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayReadyFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayReadyFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_READY_FAILURE_EXCEPTION, cause);
    }
}