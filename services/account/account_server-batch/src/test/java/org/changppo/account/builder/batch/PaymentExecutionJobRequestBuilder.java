package org.changppo.account.builder.batch;


import org.changppo.account.payment.dto.PaymentExecutionJobRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

public class PaymentExecutionJobRequestBuilder {
    public static PaymentExecutionJobRequest buildPaymentExecutionJobRequest(Long memberId, BigDecimal amount, LocalDate date) {
        return new PaymentExecutionJobRequest(memberId, amount, date);
    }
}
