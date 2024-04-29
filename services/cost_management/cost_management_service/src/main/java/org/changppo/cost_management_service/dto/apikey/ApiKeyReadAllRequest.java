package org.changppo.cost_management_service.dto.apikey;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyReadAllRequest {
    @NotNull(message = "{apiKeyReadAllRequest.firstApiKeyId.notNull}")
    @Positive(message = "{apiKeyReadAllRequest.firstApiKeyId.positive}")
    @Max(value = Long.MAX_VALUE - 1, message = "{apiKeyReadAllRequest.firstApiKeyId.maxValue}")
    private Long firstApiKeyId;

    @NotNull(message = "{apiKeyReadAllRequest.size.notNull}")
    @Positive(message = "{apiKeyReadAllRequest.size.positive}")
    @Max(value = Integer.MAX_VALUE - 1, message = "{apiKeyReadAllRequest.size.maxValue}")
    private Integer size;
}
