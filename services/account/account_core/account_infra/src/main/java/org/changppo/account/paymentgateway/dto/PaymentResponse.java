package org.changppo.account.paymentgateway.dto;

public interface PaymentResponse {
    String getKey();
    String getCardType();
    String getCardIssuerCorporation();
    String getCardBin();
}
