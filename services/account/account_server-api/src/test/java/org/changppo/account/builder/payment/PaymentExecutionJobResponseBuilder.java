package org.changppo.account.builder.payment;

import org.changppo.account.batch.dto.PaymentExecutionJobResponse;

public class PaymentExecutionJobResponseBuilder {
    public static PaymentExecutionJobResponse buildPaymentExecutionJobResponse() {
        {
            return new PaymentExecutionJobResponse("key", "CardType", "cardIssuerCorporation", "cardBin");
        }
    }
}
