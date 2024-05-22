package org.changppo.monioring.server.ratelimit;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.server.exception.ErrorCode;
import org.changppo.monioring.server.ratelimit.context.AbsentApiRateContext;
import org.changppo.monioring.server.ratelimit.context.InvalidApiRateContext;
import org.changppo.monioring.server.ratelimit.context.ValidApiRateContext;
import org.changppo.monioring.server.utils.SendErrorUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Slf4j
@Getter @Setter
@ConfigurationProperties("spring.cloud.gateway.api-rate-limiter")
public class ApiRateLimiterGatewayFilterFactory extends AbstractGatewayFilterFactory<ApiRateLimiterGatewayFilterFactory.Config> {

    public static final String EMPTY_KEY = "____EMPTY_KEY__";
    private final ApiRateLimiter defaultRateLimiter;
    private final ApiRateContextResolver defaultContextResolver;
    private boolean denyEmptyKey = true;
    private Long defaultRequestedTokens = 1L;
    private HttpStatus emptyKeyStatus = HttpStatus.UNAUTHORIZED;
    private HttpStatus invalidKeyStatus = HttpStatus.FORBIDDEN;

    public ApiRateLimiterGatewayFilterFactory(ApiRateLimiter defaultRateLimiter, ApiRateContextResolver defaultContextResolver) {
        super(Config.class);
        this.defaultRateLimiter = defaultRateLimiter;
        this.defaultContextResolver = defaultContextResolver;
    }

    @Override
    public GatewayFilter apply(Config config) {
        ApiRateContextResolver rateContextResolver = getOrDefault(config.getContextResolver(), this.defaultContextResolver);
        ApiRateLimiter rateLimiter = getOrDefault(config.getRateLimiter(), this.defaultRateLimiter);
        boolean denyEmptyKey = getOrDefault(config.getDenyEmptyKey(), this.denyEmptyKey);
        HttpStatus emptyKeyStatus = getOrDefault(config.getEmptyKeyStatus(), this.emptyKeyStatus);
        HttpStatus invalidKeyStatus = getOrDefault(config.getInvalidKeyStatus(), this.invalidKeyStatus);
        HttpStatus notAllowedStatus = getOrDefault(config.getNotAllowedStatus(), HttpStatus.TOO_MANY_REQUESTS);
        Long requestedTokens = Math.max(0,getOrDefault(config.getRequestedTokens(), 1L));

        return (exchange, chain) -> rateContextResolver.resolve(exchange).flatMap(rateContext -> {
            if (!(rateContext instanceof ValidApiRateContext)) {
                if (rateContext instanceof AbsentApiRateContext) {
                    if (denyEmptyKey) {
                        log.debug("Deny because of empty key, response status: {}", emptyKeyStatus);
                        return SendErrorUtil.sendError(exchange, ErrorCode.API_KEY_NOT_FOUND);
                    }
                    return chain.filter(exchange);
                }
                if (rateContext instanceof InvalidApiRateContext) {
                    return SendErrorUtil.sendError(exchange, ErrorCode.INVALID_API_KEY);
                }
                // should never happen
                log.error("Unknown ApiRateContext type: {}", rateContext.getClass().getName());
                ServerWebExchangeUtils.setResponseStatus(exchange, emptyKeyStatus);
                return exchange.getResponse().setComplete();
            }

            ValidApiRateContext context = (ValidApiRateContext) rateContext;
            String routeId = config.getRouteId();
            if (routeId == null) {
                Route route = exchange.getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
                routeId = route.getId();
            }
            return rateLimiter.isAllowed(routeId, context, requestedTokens).flatMap(response -> {
                for (Map.Entry<String, String> header : response.getHeaders().entrySet()) {
                    exchange.getResponse().getHeaders().add(header.getKey(), header.getValue());
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
        private ApiRateContextResolver contextResolver;
        private ApiRateLimiter rateLimiter;
        private Long requestedTokens = 1L;
        private HttpStatus notAllowedStatus = HttpStatus.TOO_MANY_REQUESTS;
        private Boolean denyEmptyKey = Boolean.FALSE;
        private HttpStatus emptyKeyStatus = HttpStatus.UNAUTHORIZED;
        private HttpStatus invalidKeyStatus = HttpStatus.FORBIDDEN;
        private String routeId;

    }

}
