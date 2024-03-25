package org.changppo.gateway;

import org.springframework.beans.factory.annotation.Qualifier;
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


    @Bean("apiRateContextResolver")
    public ApiRateContextResolver apiRateContextResolver() {
        // TODO : 추후 기능 구현시 올바른 구현체로 변경
        return new MockApiRateContextResolver();
    }

    @Bean
    public ApiRateLimiterGatewayFilterFactory requestRateLimiterGatewayFilterFactory(ApiRateLimiter rateLimiter,
                                                                                         ApiRateContextResolver resolver) {
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
