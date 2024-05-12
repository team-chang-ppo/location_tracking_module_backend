package org.changppo.monioring.server.traceid;

import org.changppo.monioring.domain.GatewayConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class TraceIdGrantFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 트레이스 ID를 생성하여 요청 헤더에 추가한다.
        exchange.mutate().request(request -> {
            request.headers(httpHeaders -> {
                httpHeaders.add(GatewayConstant.TRACE_ID_HEADER, UUID.randomUUID().toString());
            });
        });
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
