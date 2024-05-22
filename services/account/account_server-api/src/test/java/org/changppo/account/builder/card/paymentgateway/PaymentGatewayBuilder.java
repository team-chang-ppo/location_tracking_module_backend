package org.changppo.account.builder.card.paymentgateway;

import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.type.PaymentGatewayType;

public class PaymentGatewayBuilder {
    public static PaymentGateway buildPaymentGateway(PaymentGatewayType paymentGatewayType) {
        return new PaymentGateway(paymentGatewayType);
    }
}
