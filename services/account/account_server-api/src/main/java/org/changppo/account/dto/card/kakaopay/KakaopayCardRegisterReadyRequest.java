package org.changppo.account.dto.card.kakaopay;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.account.aop.ContextInjectionAspect;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayCardRegisterReadyRequest implements ContextInjectionAspect.AssignMemberId{

    @Null(message = "{kakaoCardRegisterReadyRequest.memberId.null}")
    private Long memberId;

}
