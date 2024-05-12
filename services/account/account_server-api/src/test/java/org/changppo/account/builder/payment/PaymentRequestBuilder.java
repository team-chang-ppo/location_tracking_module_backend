package org.changppo.account.builder.payment;

import org.changppo.account.dto.payment.PaymentReadAllRequest;

public class PaymentRequestBuilder {
    public static PaymentReadAllRequest buildPaymentReadAllRequest(Long lastPaymentId, Integer size) {
        return new PaymentReadAllRequest(lastPaymentId, size);
    }
}
