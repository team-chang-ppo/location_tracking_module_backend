package org.changppo.cost_management_service.service.paymentgateway;

import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.KakaopayPaymentRequest;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;

public abstract class PaymentGatewayClient {

    public abstract void inactive(String key);

    public abstract void payment(KakaopayPaymentRequest req);

    protected abstract PaymentGatewayType getSupportedPaymentGateway();

    public final boolean supports(PaymentGatewayType paymentGatewayType) {
        return getSupportedPaymentGateway() == paymentGatewayType;
    }
}
