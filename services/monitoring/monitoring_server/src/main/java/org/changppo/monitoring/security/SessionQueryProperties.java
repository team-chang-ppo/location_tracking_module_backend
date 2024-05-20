package org.changppo.monitoring.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "session-query")
public class SessionQueryProperties implements InitializingBean {
    private String sessionQueryEndpoint;
    private String sessionCookieName = "JSESSIONID";

    @Override
    public void afterPropertiesSet() throws Exception {
        if (sessionQueryEndpoint == null) {
            throw new IllegalArgumentException("sessionQueryEndpoint must be set");
        }
        if (sessionCookieName == null) {
            throw new IllegalArgumentException("sessionCookieName must be set");
        }
    }
}
