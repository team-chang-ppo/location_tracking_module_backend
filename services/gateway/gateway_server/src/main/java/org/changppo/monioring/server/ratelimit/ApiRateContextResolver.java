package org.changppo.monioring.server.ratelimit;

import org.changppo.monioring.server.ratelimit.context.ApiRateContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface ApiRateContextResolver {
    /**
     * API 키에 대한 Rate Context를 조회한다.
     * @param exchange ServerWebExchange
     * @return Rate Context
     */
    Mono<ApiRateContext> resolve(ServerWebExchange exchange);
}
