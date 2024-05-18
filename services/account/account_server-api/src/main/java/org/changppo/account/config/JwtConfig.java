package org.changppo.account.config;

import org.changppo.utils.jwt.TokenKeyProperties;
import org.changppo.utils.jwt.apikey.JwtHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TokenKeyProperties.class)
public class JwtConfig {

    @Bean
    public JwtHandler jwtHandler(TokenKeyProperties tokenKeyProperties) {
        return new JwtHandler(tokenKeyProperties);
    }
}
