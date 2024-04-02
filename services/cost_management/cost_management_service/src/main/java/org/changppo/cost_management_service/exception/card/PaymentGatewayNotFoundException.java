package org.changppo.cost_management_service.exception.card;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class PaymentGatewayNotFoundException extends CardBusinessException {
    public PaymentGatewayNotFoundException() {
        super(ExceptionType.PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION);
    }
}
