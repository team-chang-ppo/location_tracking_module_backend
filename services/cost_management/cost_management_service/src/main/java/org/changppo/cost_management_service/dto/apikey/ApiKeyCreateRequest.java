package org.changppo.cost_management_service.dto.apikey;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyCreateRequest {
    @Null(message = "{apiKeyCreateRequest.memberId.null}")
    private Long memberId;
}
