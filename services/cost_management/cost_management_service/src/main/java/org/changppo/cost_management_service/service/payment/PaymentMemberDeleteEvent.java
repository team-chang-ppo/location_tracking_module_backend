package org.changppo.cost_management_service.service.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.cost_management_service.entity.member.Member;

@Data
@AllArgsConstructor
public class PaymentMemberDeleteEvent {
    private Member member;
}
