package org.changppo.cost_management_service.dto.apikey;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.aop.ContextInjectionAspect;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyCreateRequest implements ContextInjectionAspect.AssignMemberId {
    @Null(message = "{apiKeyCreateRequest.memberId.null}")
    private Long memberId;
}
