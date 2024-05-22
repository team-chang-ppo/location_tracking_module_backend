package org.changppo.monitoring.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "cost.query")
public class CostQueryProperties {
    private Integer maxDurationDays = 60;

    public Duration getMaxDuration() {
        return Duration.ofDays(maxDurationDays);
    }

    public void setMaxDurationDays(Integer maxDurationDays) {
        this.maxDurationDays = maxDurationDays;
    }



}
