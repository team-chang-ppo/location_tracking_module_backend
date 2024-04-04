package org.changppo.cost_management_service.service.paymentgateway;

import org.changppo.cost_management_service.entity.card.PaymentGatewayType;

public abstract class PaymentGatewayClient {

    public abstract void inactive(String key);

    protected abstract PaymentGatewayType getSupportedPaymentGateway();

    public final boolean supports(PaymentGatewayType paymentGatewayType) {
        return getSupportedPaymentGateway() == paymentGatewayType;
    }
}
