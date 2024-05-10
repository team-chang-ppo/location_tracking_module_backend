package org.changppo.account.scheduled.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "quartz")
public class QuartzProperties {
    private String properties;
}
