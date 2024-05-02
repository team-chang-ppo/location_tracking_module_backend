package org.changppo.account.service.event.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.type.PaymentStatus;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publishEvent(Payment payment) {  //TODO. 알림 서비스
        if (payment.getStatus() == PaymentStatus.FAILED) {
            publishPaymentFailedEvent(payment);
        } else if (payment.getStatus() == PaymentStatus.COMPLETED_PAID) {
            publishPaymentCompletedEvent(payment);
        }
    }

    private void publishPaymentFailedEvent(Payment payment) {
        publisher.publishEvent(new PaymentFailedEvent(payment.getMember()));
    }

    private void publishPaymentCompletedEvent(Payment payment) {
        publisher.publishEvent(new PaymentCompletedEvent(payment.getMember()));
    }
}
