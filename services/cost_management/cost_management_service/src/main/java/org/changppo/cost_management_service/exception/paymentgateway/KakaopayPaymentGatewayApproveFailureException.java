package org.changppo.cost_management_service.exception.paymentgateway;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class KakaopayPaymentGatewayApproveFailureException extends PaymentGatewayBusinessException {
    public KakaopayPaymentGatewayApproveFailureException(Throwable cause) {
        super(ExceptionType.KAKAOPAY_PAYMENT_GATEWAY_APPROVE_FAILURE_EXCEPTION, cause);
    }
}
