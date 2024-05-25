package org.changppo.account.builder.batch;


import org.changppo.account.batch.dto.PaymentExecutionJobResponse;

public class PaymentExecutionJobResponseBuilder {
    public static PaymentExecutionJobResponse buildPaymentExecutionJobRequest(String key, String cardType, String cardIssuerCorporation, String cardBin) {
        return new PaymentExecutionJobResponse(key, cardType, cardIssuerCorporation, cardBin);
    }
}
