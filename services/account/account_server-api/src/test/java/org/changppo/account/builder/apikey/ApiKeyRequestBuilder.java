package org.changppo.account.builder.apikey;

import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;

public class ApiKeyRequestBuilder {
    public static ApiKeyCreateRequest buildApiKeyCreateRequest(Long memberId) {
        return new ApiKeyCreateRequest(memberId);
    }

    public static ApiKeyReadAllRequest buildApiKeyReadAllRequest(Long firstApiKeyId, Integer size) {
        return new ApiKeyReadAllRequest(firstApiKeyId, size);
    }
}
