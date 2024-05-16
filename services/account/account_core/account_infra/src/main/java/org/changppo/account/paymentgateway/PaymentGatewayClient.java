package org.changppo.account.paymentgateway;

import org.changppo.account.paymentgateway.dto.PaymentRequest;
import org.changppo.account.paymentgateway.dto.PaymentResponse;
import org.changppo.account.response.ClientResponse;
import org.changppo.account.type.PaymentGatewayType;

public abstract class PaymentGatewayClient {

    public abstract void inactive(String key);

    public abstract ClientResponse<PaymentResponse> payment(PaymentRequest req);

    protected abstract PaymentGatewayType getSupportedPaymentGateway();

    public final boolean supports(PaymentGatewayType paymentGatewayType) {
        return getSupportedPaymentGateway() == paymentGatewayType;
    }
}
