package org.changppo.account.builder.batch;


import org.changppo.account.payment.dto.PaymentExecutionJobRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentExecutionJobRequestBuilder {
    public static PaymentExecutionJobRequest buildPaymentExecutionJobRequest(Long memberId, BigDecimal amount, LocalDateTime date) {
        return new PaymentExecutionJobRequest(memberId, amount, date);
    }
}
