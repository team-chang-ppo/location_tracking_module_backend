package org.changppo.account.config;

import org.changppo.utils.jwt.JwtProperties;
import org.changppo.utils.jwt.apikey.apiKeyJwtHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtConfig {

    @Bean
    public apiKeyJwtHandler apiKeyJwtHandler(JwtProperties jwtProperties) {
        return new apiKeyJwtHandler(jwtProperties);
    }
}
