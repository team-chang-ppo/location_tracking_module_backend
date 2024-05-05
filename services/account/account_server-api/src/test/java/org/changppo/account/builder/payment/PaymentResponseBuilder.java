package org.changppo.account.builder.payment;

import org.changppo.account.paymentgateway.dto.PaymentResponse;

public class PaymentResponseBuilder {
    public static PaymentResponse buildPaymentResponse() {
        return new PaymentResponse() {
            @Override
            public String getKey() {
                return "12345";
            }

            @Override
            public String getCardType() {
                return "Visa";
            }

            @Override
            public String getCardIssuerCorporation() {
                return "Issuer Corp";
            }

            @Override
            public String getCardBin() {
                return "123456";
            }
        };
    }
}
