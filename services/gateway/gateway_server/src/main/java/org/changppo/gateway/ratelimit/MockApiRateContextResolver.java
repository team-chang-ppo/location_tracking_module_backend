package org.changppo.gateway.ratelimit;

import org.changppo.gateway.apikey.ApiKey;
import org.changppo.gateway.ratelimit.context.AbsentApiRateContext;
import org.changppo.gateway.ratelimit.context.ApiRateContext;
import org.changppo.gateway.ratelimit.context.ValidApiRateContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 다음 개발시까지 목업 해두기 위해 사용
 */
public class MockApiRateContextResolver implements ApiRateContextResolver {
    private final static AbsentApiRateContext ABSENT_API_RATE_CONTEXT = new AbsentApiRateContext();
    private final static ValidApiRateContext VALID_API_RATE_CONTEXT_FOR_MOCKED_TEST = new ValidApiRateContext("test-key", 10, 300);

    @Override
    public Mono<ApiRateContext> resolve(ServerWebExchange exchange) {
        Object attribute = exchange.getAttribute(ApiKey.class.getName());
        if (attribute == null) {
            return Mono.just(ABSENT_API_RATE_CONTEXT);
        }
        // 실제 상황에서는 fetching 과 검증이 일어나야함
        return Mono.just(VALID_API_RATE_CONTEXT_FOR_MOCKED_TEST);


    }
}
