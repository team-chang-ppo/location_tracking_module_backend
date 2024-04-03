package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayFailException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayFailException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_FAIL_EXCEPTION, cause);
    }
}