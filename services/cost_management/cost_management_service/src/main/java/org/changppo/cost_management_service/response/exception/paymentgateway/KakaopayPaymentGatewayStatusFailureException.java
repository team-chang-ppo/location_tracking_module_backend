package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayStatusFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayStatusFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_STATUS_FAILURE_EXCEPTION, cause);
    }
}