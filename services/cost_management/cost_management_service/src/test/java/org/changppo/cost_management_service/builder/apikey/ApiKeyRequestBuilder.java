package org.changppo.cost_management_service.builder.apikey;

import org.changppo.cost_management_service.dto.apikey.ApiKeyCreateRequest;
import org.changppo.cost_management_service.dto.apikey.ApiKeyReadAllRequest;

public class ApiKeyRequestBuilder {
    public static ApiKeyCreateRequest buildApiKeyCreateRequest(Long memberId) {
        return new ApiKeyCreateRequest(memberId);
    }

    public static ApiKeyReadAllRequest buildApiKeyReadAllRequest(Long firstApiKeyId, Integer size, Long memberId) {
        return new ApiKeyReadAllRequest(firstApiKeyId, size, memberId);
    }
}
