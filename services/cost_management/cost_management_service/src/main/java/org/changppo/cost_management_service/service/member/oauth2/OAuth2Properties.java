package org.changppo.cost_management_service.service.member.oauth2;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2Properties {
    private Kakao kakao;
    @Getter
    @Setter
    public static class Kakao {
        private String adminKey;
    }
}