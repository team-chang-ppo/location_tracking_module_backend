package org.changppo.account.builder.apikey;

import org.changppo.account.dto.apikey.ApiKeyValidationResponse;

public class ApiKeyResponseBuilder {
    public static ApiKeyValidationResponse buildApiKeyValidationResponse(boolean isValid) {
        return new ApiKeyValidationResponse(isValid);
    }
}
