package org.changppo.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.cloud.gateway.config.conditional.ConditionalOnEnabledFilter;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.List;

@Configuration
public class RateLimiterConfig {
    @Bean("apiKeyRateLimiter")
    @Primary
    @SuppressWarnings("unchecked")
    public ApiRateLimiter apiKeyRateLimiter(
            ReactiveStringRedisTemplate redisTemplate,
            ApiRateContextResolver apiRateContextResolver,
            @Qualifier("customRedisRequestRateLimiterScript") RedisScript redisScript
    ) {
        return new ApiRedisRateLimiter(redisTemplate, redisScript, apiRateContextResolver);
    }

    @Bean("apiKeyResolver")
    public KeyResolver apiKeyResolver(
    ) {
        return new ApiKeyResolver();
    }

    @Bean("apiRateContextResolver")
    public ApiRateContextResolver apiRateContextResolver() {
        return new MockApiRateContextResolver();
    }

    @Bean
    @ConditionalOnBean({ ApiRateLimiter.class, KeyResolver.class })
    @ConditionalOnEnabledFilter
    public ApiRateLimiterGatewayFilterFactory requestRateLimiterGatewayFilterFactory(ApiRateLimiter rateLimiter,
                                                                                         KeyResolver resolver) {
        return new ApiRateLimiterGatewayFilterFactory(rateLimiter, resolver);
    }

    @Bean("customRedisRequestRateLimiterScript")
    @SuppressWarnings("unchecked")
    public RedisScript customRedisRequestRateLimiterScript() {
        DefaultRedisScript redisScript = new DefaultRedisScript<>();
        ClassPathResource resource = new ClassPathResource("script/request_rate_limiter.lua");
        if (!resource.exists()) {
            throw new IllegalArgumentException("script/request_rate_limiter.lua not found");
        }
        redisScript.setScriptSource(
                new ResourceScriptSource(resource));
        redisScript.setResultType(List.class);
        return redisScript;
    }
}
