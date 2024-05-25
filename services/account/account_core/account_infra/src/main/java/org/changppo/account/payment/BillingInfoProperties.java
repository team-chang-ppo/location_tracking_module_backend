package org.changppo.account.payment;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "billing")
public class BillingInfoProperties {
    private String url;
}
