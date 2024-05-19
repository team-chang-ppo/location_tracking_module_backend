package org.changppo.monioring.server.apikey;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.server.exception.ErrorCode;
import org.changppo.monioring.server.exception.ErrorResponse;
import org.changppo.monioring.server.exception.InvalidApiKeyException;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class ApiKeyResolverFilter implements GlobalFilter, Ordered {
    private final ApiKeyResolver apiKeyResolver;
    private final ApiKeyIdManager apiKeyIdManager;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            //토큰이 없을 경우, 그냥 바로, chain.filter(exchange)를 호출한다.
        return apiKeyResolver.resolve(exchange).filterWhen(apiKey -> {
                    Long id = apiKey.apiKeyId();
                    return apiKeyIdManager.isValidApiKeyId(id);
                })
                .doOnNext(apiKey -> {
                    exchange.getAttributes().put(ApiKey.class.getName(), apiKey);
                })
                .then(chain.filter(exchange))
                .onErrorResume(InvalidApiKeyException.class, e -> handleInvalidApiKeyException(exchange, e));
    }

    protected Mono<Void> handleInvalidApiKeyException(ServerWebExchange exchange, InvalidApiKeyException e) {
        log.error("Invalid API Key", e);
        // 에러 응답을 반환한다.
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse errorResponse = new ErrorResponse(errorCode);
        exchange.getResponse().setStatusCode(errorCode.getHttpStatus());
        Mono<DataBuffer> bufferMono = Mono.just(exchange.getResponse().bufferFactory().wrap(errorResponse.toJsonString().getBytes()));
        return exchange.getResponse().writeWith(bufferMono);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 10;
    }
}
