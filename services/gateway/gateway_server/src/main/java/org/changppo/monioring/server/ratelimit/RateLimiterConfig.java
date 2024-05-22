package org.changppo.monioring.server.ratelimit;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
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

    @Profile("local")
    @Bean("apiRateContextResolver")
    public ApiRateContextResolver apiRateContextResolver() {
        return new MockApiRateContextResolver();
    }

    @Profile("prod")
    @Bean("apiRateContextResolver")
    public ApiRateContextResolver prodApiRateContextResolver(
            RateLimiterConfigurationProperties rateLimiterConfigurationProperties
    ) {
        return new ApiKeyRateContextResolver(rateLimiterConfigurationProperties);
    }

    @Bean
    public ApiRateLimiterGatewayFilterFactory requestRateLimiterGatewayFilterFactory(ApiRateLimiter rateLimiter,
                                                                                     ApiRateContextResolver resolver
    ) {
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
