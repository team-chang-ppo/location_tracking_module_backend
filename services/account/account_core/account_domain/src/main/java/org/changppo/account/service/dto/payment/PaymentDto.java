package org.changppo.account.service.dto.payment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.type.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class PaymentDto {
    private Long id;
    private BigDecimal amount;
    private PaymentStatus status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate startedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate endedAt;
    private PaymentCardInfo cardInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public PaymentDto(Long id, BigDecimal amount, PaymentStatus status, LocalDate startedAt, LocalDate endedAt, PaymentCardInfo cardInfo, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.cardInfo = cardInfo;
        this.createdAt = createdAt;
    }
}
