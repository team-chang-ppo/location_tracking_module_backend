package org.changppo.monioring.server.ratelimit;

import lombok.RequiredArgsConstructor;
import org.changppo.monioring.server.apikey.ApiKey;
import org.changppo.monioring.server.ratelimit.context.AbsentApiRateContext;
import org.changppo.monioring.server.ratelimit.context.ApiRateContext;
import org.changppo.monioring.server.ratelimit.context.InvalidApiRateContext;
import org.changppo.monioring.server.ratelimit.context.ValidApiRateContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ApiKeyRateContextResolver implements ApiRateContextResolver{
    private final static AbsentApiRateContext ABSENT_API_RATE_CONTEXT = new AbsentApiRateContext();
    private final static InvalidApiRateContext UNKNOWN_GRADE_API_RATE_CONTEXT = new InvalidApiRateContext("unknown-grade", new String[]{"unknown grade"});
    private final RateLimiterConfigurationProperties rateLimiterConfigurationProperties;

    @Override
    public Mono<ApiRateContext> resolve(ServerWebExchange exchange) {
        Object attribute = exchange.getAttribute(ApiKey.class.getName());
        if (attribute == null) {
            return Mono.just(ABSENT_API_RATE_CONTEXT);
        }
        ApiKey apiKey = (ApiKey) attribute;
        String grade = apiKey.gradeType().name();
        if (!rateLimiterConfigurationProperties.getCapacities().containsKey(grade)) {
            // 만약 모르는 grade가 들어오면
            return Mono.just(UNKNOWN_GRADE_API_RATE_CONTEXT);
        }
        var capacity = rateLimiterConfigurationProperties.getCapacities().get(grade);
        return Mono.just(new ValidApiRateContext(apiKey.apiKeyId().toString(), capacity.getReplenishRate(), capacity.getBurstCapacity()));
    }

}
