package org.changppo.account.dto.payment.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.member.Member;

@Data
@AllArgsConstructor
public class PaymentMemberDeleteEvent {
    private Member member;
}