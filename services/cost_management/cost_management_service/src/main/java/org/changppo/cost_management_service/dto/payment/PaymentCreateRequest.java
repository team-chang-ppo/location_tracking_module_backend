package org.changppo.cost_management_service.dto.payment;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.member.Member;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentCreateRequest {
    @NotNull(message = "{paymentCreateRequest.key.notNull}")
    private String key;
    @NotNull(message = "{paymentCreateRequest.amount.notNull}")
    private Integer amount;
    @NotNull(message = "{paymentCreateRequest.card.notNull}")
    private Card card;
    @NotNull(message = "{paymentCreateRequest.member.notNull}")
    private Member member;
    @NotNull(message = "{paymentCreateRequest.startedAt.notNull}")
    private LocalDateTime startedAt;
    @NotNull(message = "{paymentCreateRequest.endedAt.notNull}")
    private LocalDateTime endedAt;
}
