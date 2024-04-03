package org.changppo.cost_management_service.response.exception.paymentgateway;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class KakaopayPaymentGatewayApproveFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayApproveFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION, cause);
    }
}
