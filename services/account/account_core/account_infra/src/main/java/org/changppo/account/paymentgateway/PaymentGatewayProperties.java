package org.changppo.account.paymentgateway;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
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
