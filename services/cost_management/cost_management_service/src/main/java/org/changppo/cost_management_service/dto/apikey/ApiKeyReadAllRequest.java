package org.changppo.cost_management_service.dto.apikey;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyReadAllRequest {
    @Positive(message = "{apiKeyReadAllRequest.lastApiKeyId.positive}")
    private Long lastApiKeyId = Long.MAX_VALUE;

    @NotNull(message = "{apiKeyReadAllRequest.size.notNull}")
    @Positive(message = "{apiKeyReadAllRequest.size.positive}")
    private Integer size;

    @Null(message = "{apiKeyReadAllRequest.memberId.null}")
    private Long memberId;
}
