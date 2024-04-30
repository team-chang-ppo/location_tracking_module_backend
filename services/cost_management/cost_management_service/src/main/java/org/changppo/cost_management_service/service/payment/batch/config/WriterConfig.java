package org.changppo.cost_management_service.service.payment.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher publisher;

    @Bean
    public ItemWriter<Payment> paymentWriter() {
        return payments -> payments.forEach(payment -> {
            payment.publishCreatedEvent(publisher);
            paymentRepository.save(payment);
        });
    }
}