package org.changppo.cost_management_service.service.paymentgateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "paymentgateway")
public class PaymentGatewayProperties {
    private Kakaopay kakaopay;

    @Getter
    @Setter
    public static class Kakaopay {
        private String cid;
        private String ccid;
        private String secretKey;
    }
}
