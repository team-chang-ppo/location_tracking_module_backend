package org.changppo.utils.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tokenkey")
public class TokenKeyProperties {
    private String secret;
}
