package org.changppo.cost_management_service.service.payment.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WriterConfig {

    private final PaymentRepository paymentRepository;

    @Bean
    public ItemWriter<Payment> paymentWriter() {
        return paymentRepository::saveAll;
    }
}