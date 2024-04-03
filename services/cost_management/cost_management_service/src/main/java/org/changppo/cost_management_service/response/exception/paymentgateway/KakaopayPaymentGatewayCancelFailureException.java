package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayCancelFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayCancelFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_CANCEL_FAILURE_EXCEPTION, cause);
    }
}