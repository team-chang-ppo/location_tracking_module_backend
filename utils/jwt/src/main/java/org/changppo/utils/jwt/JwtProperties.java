package org.changppo.utils.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {  // TODO. 건우가 trackingToken에 맞게 구현
    private ApiKey apiKey;
    private TrackingToken trackingToken;

    @Getter
    @Setter
    public static class ApiKey {
        private String secretKey;
    }

    @Getter
    @Setter
    public static class TrackingToken {
        private String secretKey;
        private String issuer;
        private int expireIn;
    }
}
