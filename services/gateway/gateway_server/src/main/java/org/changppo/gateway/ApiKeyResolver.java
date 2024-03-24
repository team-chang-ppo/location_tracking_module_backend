package org.changppo.gateway;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static org.changppo.gateway.ApiRateLimiterGatewayFilterFactory.EMPTY_KEY;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyResolver implements KeyResolver {

    @Override
    public Mono<String> resolve(ServerWebExchange exchange) {
        String apiKey = exchange.getRequest().getHeaders().getFirst(GatewayConstant.API_KEY_HEADER);
        return Mono.just(Objects.requireNonNullElse(apiKey, EMPTY_KEY));
    }
}
