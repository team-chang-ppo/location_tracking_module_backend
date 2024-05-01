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
public class KakaopayCardRegisterApproveRequest implements ContextInjectionAspect.AssignMemberId {

    @NotNull(message = "{kakaopayCardRegisterApproveRequest.partnerOrderId.notNull}")
    private String partner_order_id;
    @NotNull(message = "{kakaopayCardRegisterApproveRequest.pgToken.notNull}")
    private String pg_token;
    @Null(message = "{kakaopayCardRegisterApproveRequest.memberId.null}")
    private Long memberId;
}
