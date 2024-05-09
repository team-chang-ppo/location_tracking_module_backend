package org.changppo.account.service.apikey.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "tokenkey")
public class TokenKeyProperties {
    private String secret;
}
