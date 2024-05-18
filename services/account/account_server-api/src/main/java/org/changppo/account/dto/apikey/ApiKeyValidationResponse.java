package org.changppo.account.dto.apikey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiKeyValidationResponse {
    private boolean valid;
}
