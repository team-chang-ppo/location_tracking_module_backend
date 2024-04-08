package org.changppo.cost_management_service.service.payment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.payment.PaymentCreateRequest;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
@Transactional(readOnly = true)
public class PaymentService {
    private final PaymentRepository paymentRepository;
    @Transactional
    public void create(@Valid PaymentCreateRequest req) {
        Payment payment = Payment.builder()
                .key(req.getKey())
                .amount(req.getAmount())
                .card(req.getCard())
                .member(req.getMember())
                .startedAt(req.getStartedAt())
                .endedAt(req.getEndedAt())
                .build();
        paymentRepository.save(payment);
    }
}
