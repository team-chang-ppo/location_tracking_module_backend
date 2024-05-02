package org.changppo.account.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentExecutionJobRequest {
    private Long memberId;
    private BigDecimal amount;
    private LocalDateTime date;
}
