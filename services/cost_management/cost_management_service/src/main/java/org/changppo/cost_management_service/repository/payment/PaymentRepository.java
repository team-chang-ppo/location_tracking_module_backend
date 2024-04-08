package org.changppo.cost_management_service.repository.payment;

import org.changppo.cost_management_service.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
