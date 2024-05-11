package org.changppo.account.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "batch")
public class PaymentExecutionJobProperties {
    private String url;
}
