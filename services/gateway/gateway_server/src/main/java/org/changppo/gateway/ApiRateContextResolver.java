package org.changppo.gateway;

import reactor.core.publisher.Mono;

public interface ApiRateContextResolver {
    /**
     * API 키에 대한 Rate Context를 조회한다.
     * @param apiKey  API Key
     * @param path    API path
     * @return Rate Context, 만약 존재하지 않는 경우 Mono.empty()
     */
    Mono<ApiRateContext> resolve(String path, String apiKey);
}
