package org.changppo.cost_management_service.service.member.oauth;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oauth")
public class OAuthProperties {
    private Kakao kakao;
    @Getter
    @Setter
    public static class Kakao {
        private String adminKey;
    }
}