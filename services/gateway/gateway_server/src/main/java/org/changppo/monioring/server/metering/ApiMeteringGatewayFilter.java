package org.changppo.monioring.server.metering;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.ApiUsageEventPayLoad;
import org.changppo.monioring.domain.GatewayConstant;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.UUID;
import java.util.function.Function;


@Slf4j
public class ApiMeteringGatewayFilter implements GlobalFilter, Ordered {
    private final ApiMeteringEventPublisher apiMeteringEventPublisher;

    public ApiMeteringGatewayFilter(ApiMeteringEventPublisher apiMeteringEventPublisher) {
        this.apiMeteringEventPublisher = apiMeteringEventPublisher;
    }


    protected<T, R> R doIfNotNull(T value, Function<T, R> function) {
        return value == null ? null : function.apply(value);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        final ApiUsageEventPayLoad event = new ApiUsageEventPayLoad();
        event.setEventId(UUID.randomUUID().toString());
        event.setRequestTime(Instant.now());
        event.setRequestProtocol(request.getURI().getScheme());
        event.setRequestMethod(request.getMethod().name());
        event.setRequestUri(request.getURI().getPath());
        //TODO 나중에, 바꿔야함
        // 테스트를 위해 만약 localhost 면 googleIp로 바꿔서 기록 ㅋㅋ
        String clientIp = doIfNotNull(request.getRemoteAddress(), address -> address.getAddress().getHostAddress());
        if(clientIp == null
                || clientIp.equals("0:0:0:0:0:0:0:1") // ipv6
                || clientIp.equals("127.0.0.1") // ipv4
                || clientIp.equals("localhost") // localhost
                || clientIp.startsWith("192.168.") // 내부망
                || clientIp.startsWith("10.") // 내부망
                || clientIp.startsWith("172.") // 내부망
        ) {
            clientIp = "172.217.161.206"; // google.com
        }

        event.setClientIp(clientIp);
        event.setClientAgent(request.getHeaders().getFirst("User-Agent"));
        event.setApiKey(request.getHeaders().getFirst(GatewayConstant.API_KEY_HEADER));
        event.setTraceId(request.getHeaders().getFirst(GatewayConstant.TRACE_ID_HEADER));

        return chain.filter(exchange)
                .then(Mono.defer(() -> {
                    // 응답 시간을 설정한다.
                    event.setResponseTime(Instant.now());
                    event.setResponseStatus(doIfNotNull(exchange.getResponse().getStatusCode(), HttpStatusCode::value));
                    //TODO 에러 코드 파싱 로직
                    //event.setErrorCode(doIfNotNull(exchange.getResponse().getStatusCode(), ErrorCode::valueOf));
                    return apiMeteringEventPublisher.publish(event)
                            .onErrorResume(throwable -> {
                                log.error("Failed to publish event", throwable);
                                return Mono.empty();
                            });
                }));
    }

    @Override
    public int getOrder() {
        return 0;
    }

    @Getter @Setter
    public static class Config implements HasRouteId {
        private String routeId;
    }

}
