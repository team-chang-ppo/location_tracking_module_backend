package org.changppo.gateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Getter @Setter
@ConfigurationProperties("spring.cloud.gateway.api-rate-limiter")
public class ApiRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<ApiRateLimiterGatewayFilterFactory.Config> {

    public static final String EMPTY_KEY = "____EMPTY_KEY__";
    private final ApiRateLimiter defaultRateLimiter;
    private final KeyResolver defaultKeyResolver;
    private boolean denyEmptyKey = true;
    private Long defaultRequestedTokens = 1L;
    private HttpStatus emptyKeyStatus = HttpStatus.UNAUTHORIZED;

    public ApiRateLimiterGatewayFilterFactory(ApiRateLimiter defaultRateLimiter, KeyResolver defaultKeyResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultKeyResolver = defaultKeyResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        KeyResolver keyResolver = getOrDefault(config.getKeyResolver(), defaultKeyResolver);
        ApiRateLimiter rateLimiter = getOrDefault(config.getRateLimiter(), defaultRateLimiter);
        boolean denyEmptyKey = getOrDefault(config.getDenyEmptyKey(), this.denyEmptyKey);
        HttpStatus emptyKeyStatus = getOrDefault(config.getEmptyKeyStatus(), this.emptyKeyStatus);
        HttpStatus notAllowedStatus = getOrDefault(config.getNotAllowedStatus(), HttpStatus.TOO_MANY_REQUESTS);
        Long requestedTokens = Math.max(0,getOrDefault(config.getRequestedTokens(), 1L));

        return (exchange, chain) -> keyResolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY).flatMap(key -> {
            if (EMPTY_KEY.equals(key)) {
                if (denyEmptyKey) {
                    ServerWebExchangeUtils.setResponseStatus(exchange, emptyKeyStatus);
                    return exchange.getResponse().setComplete();
                }
                return chain.filter(exchange);
            }
            String routeId = config.getRouteId();
            if (routeId == null) {
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                routeId = route.getId();
            }
            return rateLimiter.isAllowed(routeId, key, requestedTokens).flatMap(response -> {
                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                    exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
                }

                if (response.isApiKeyNotFound()) {
                    ServerWebExchangeUtils.setResponseStatus(exchange, emptyKeyStatus);
                    return exchange.getResponse().setComplete();
                }

                if (response.isAllowed()) {
                    return chain.filter(exchange);
                }

                ServerWebExchangeUtils.setResponseStatus(exchange, notAllowedStatus);
                return exchange.getResponse().setComplete();
            });
        });
    }
    private <T> T getOrDefault(T configValue, T defaultValue) {
        return (configValue != null) ? configValue : defaultValue;
    }

    @Getter @Setter
    public static class Config implements HasRouteId {
        private ApiKeyResolver keyResolver;
        private ApiRateLimiter rateLimiter;
        private Long requestedTokens = 1L;
        private HttpStatus notAllowedStatus = HttpStatus.TOO_MANY_REQUESTS;
        private Boolean denyEmptyKey;
        private HttpStatus emptyKeyStatus = HttpStatus.UNAUTHORIZED;
        private String routeId;

    }

}
