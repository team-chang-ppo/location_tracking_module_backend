package org.changppo.gateway.apikey;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Getter @Setter
@Configuration
@ConfigurationProperties(prefix = "apikey.jwt")
public class JwtConfigurationProperty implements InitializingBean {
    private static final String TEST_SECRET = "test-secret-key-o134084704380574235843528";
    private String secret = TEST_SECRET;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalArgumentException("apikey.jwt.secret is required");
        }
        if (secret.length() < 32) {
            log.warn("apikey.jwt.secret is less than 32 characters. It is recommended to use a secret with a length of 32 or more characters.");
        }

        if (TEST_SECRET.equals(secret)) {
            log.error("apikey.jwt.secret is set to the default value. Should not use the default value in production.");
        }

    }
}
