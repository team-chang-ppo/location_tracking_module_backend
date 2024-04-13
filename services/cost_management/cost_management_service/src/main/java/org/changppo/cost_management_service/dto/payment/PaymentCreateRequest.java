package org.changppo.cost_management_service.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {
    @NotNull(message = "{paymentCreateRequest.key.notNull}")
    private String key;
    @NotNull(message = "{paymentCreateRequest.amount.notNull}")
    private Integer amount;
    @NotNull(message = "{paymentCreateRequest.status.notNull}")
    private PaymentStatus status;
    @NotNull(message = "{paymentCreateRequest.startedAt.notNull}")
    private LocalDateTime startedAt;
    @NotNull(message = "{paymentCreateRequest.endedAt.notNull}")
    private LocalDateTime endedAt;
    @NotNull(message = "{paymentCreateRequest.member.notNull}")
    private Member member;
    private PaymentCardInfo cardInfo; //nullable
}