package org.changppo.monioring.server.apikey;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class ApiKeyConfig {

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
    public ApiKeyResolverFilter apiKeyContextResolverFilter(
            ApiKeyResolver apiKeyResolver,
            ApiKeyIdManager apiKeyIdManager
    ) {
        return new ApiKeyResolverFilter(apiKeyResolver, apiKeyIdManager);
    }
}
