package org.changppo.monioring.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.monioring.server.apikey.*;
import org.changppo.monioring.server.metering.ApiMeteringEventPublisher;
import org.changppo.monioring.server.metering.ApiMeteringGatewayFilterFactory;
import org.changppo.monioring.server.metering.KafkaApiMeteringEventPublisher;
import org.changppo.monioring.server.ratelimit.*;
import org.changppo.monioring.server.traceid.TraceIdGrantFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.scripting.support.ResourceScriptSource;
import reactor.core.publisher.Mono;

import java.util.List;

@Configuration
public class GatewayConfig {

    @Bean
    public ApiKeyResolver apiKeyResolver(JwtConfigurationProperty jwtConfigurationProperty) {
        return new JwtBasedApiKeyResolver(jwtConfigurationProperty);
    }

    @Bean
    public ApiKeyIdManager apiKeyIdManager() {
        // TODO : 일단 화이트 리스트 API 나올때까지는 무조건 통과하도록
        return (id) -> Mono.just(true);
    }

    @Bean
    public ApiMeteringEventPublisher apiMeteringEventPublisher(
            ReactiveKafkaProducerTemplate<String, String> kafkaProducerTemplate,
            ObjectMapper objectMapper
    ) {
        return new KafkaApiMeteringEventPublisher(kafkaProducerTemplate, objectMapper);
    }

    @Bean
    public ApiKeyResolverFilter apiKeyContextResolverFilter(
            ApiKeyResolver apiKeyResolver,
            ApiKeyIdManager apiKeyIdManager
    ) {
        return new ApiKeyResolverFilter(apiKeyResolver, apiKeyIdManager);
    }

    @Bean
    public TraceIdGrantFilter traceIdGrantFilter() {
        return new TraceIdGrantFilter();
    }

    @Bean
    public ApiMeteringGatewayFilterFactory apiMeteringGatewayFilterFactory(
            ApiMeteringEventPublisher apiMeteringEventPublisher
    ) {
        return new ApiMeteringGatewayFilterFactory(apiMeteringEventPublisher);
    }

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
