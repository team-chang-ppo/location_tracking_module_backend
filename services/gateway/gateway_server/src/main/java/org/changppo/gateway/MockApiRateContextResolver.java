package org.changppo.gateway;

import reactor.core.publisher.Mono;

/**
 * 다음 개발시까지 목업 해두기 위해 사용
 */
public class MockApiRateContextResolver implements ApiRateContextResolver {
    @Override
    public Mono<ApiRateContext> resolve(String routeId, String apiKey) {
        ApiRateContext context = new ApiRateContext();
        context.setReplenishRate(10);
        context.setBurstCapacity(300);
        return Mono.just(context);
    }
}
