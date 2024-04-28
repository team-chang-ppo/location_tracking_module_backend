package org.changppo.cost_management_service.service.payment;

import lombok.RequiredArgsConstructor;
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

}
