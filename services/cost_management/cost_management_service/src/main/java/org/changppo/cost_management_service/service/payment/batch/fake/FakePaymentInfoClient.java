package org.changppo.cost_management_service.service.payment.batch.fake;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Random;

@Component
public class FakePaymentInfoClient {
    public int getPaymentAmountForPeriod(Long memberId, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return new Random().nextInt(900) + 100;
    }
}
