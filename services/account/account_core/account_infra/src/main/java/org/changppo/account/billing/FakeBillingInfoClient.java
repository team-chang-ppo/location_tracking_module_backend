package org.changppo.account.billing;

import org.changppo.account.response.ClientResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class FakeBillingInfoClient {  //TODO. Spring Cloud OpenFeign 사용
    public ClientResponse<BigDecimal> getBillingAmountForPeriod(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        BigDecimal amount = BigDecimal.valueOf(new Random().nextLong(900) + 100);
        return ClientResponse.success(amount);
    }
}
