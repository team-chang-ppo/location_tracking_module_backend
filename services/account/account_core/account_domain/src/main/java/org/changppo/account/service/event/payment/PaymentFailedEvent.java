package org.changppo.account.service.event.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.entity.member.Member;

@Data
@AllArgsConstructor
public class PaymentFailedEvent {
    private Member member;
}
