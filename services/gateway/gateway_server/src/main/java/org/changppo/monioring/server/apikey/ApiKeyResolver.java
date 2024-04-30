package org.changppo.monioring.server.apikey;

import org.changppo.monioring.server.exception.InvalidApiKeyException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ApiKeyResolver {
    /**
     * 요청에서 API 키를 추출하여 API 키 컨텍스트를 반환한다.
     * @param exchange ServerWebExchange
     * @return API 키 컨텍스트, Mono.empty()를 반환하면 API 키가 없는 경우
     * @throws InvalidApiKeyException API 키가 유효하지 않은 경우
     */
    Mono<ApiKey> resolve(ServerWebExchange exchange);
}
