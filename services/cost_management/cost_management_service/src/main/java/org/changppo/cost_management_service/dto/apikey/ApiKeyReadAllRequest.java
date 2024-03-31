package org.changppo.cost_management_service.dto.apikey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.aop.AssignMemberIdAspect;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyReadAllRequest implements AssignMemberIdAspect.AssignMemberId {
    @NotNull(message = "{apiKeyReadAllRequest.firstApiKeyId.notNull}")
    @Positive(message = "{apiKeyReadAllRequest.firstApiKeyId.positive}")
    private Long firstApiKeyId;

    @NotNull(message = "{apiKeyReadAllRequest.size.notNull}")
    @Positive(message = "{apiKeyReadAllRequest.size.positive}")
    @Max(value = Integer.MAX_VALUE - 1, message = "{apiKeyReadAllRequest.size.maxValue}")
    private Integer size;

    @Null(message = "{apiKeyReadAllRequest.memberId.null}")
    private Long memberId;
}
