package org.changppo.account.dto.card.kakaopay;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.account.aop.ContextInjectionAspect;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayCardRegisterFailRequest implements ContextInjectionAspect.AssignMemberId {

    @NotNull(message = "{kakaopayCardRegisterFailRequest.partnerOrderId.notNull}")
    private String partner_order_id;
    @Null(message = "{kakaopayCardRegisterFailRequest.memberId.null}")
    private Long memberId;
}
