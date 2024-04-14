package org.changppo.cost_management_service.repository.payment;

import org.changppo.cost_management_service.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteAllByMemberId(Long memberId);
    Optional<Payment> findFirstByMemberIdOrderByEndedAtDesc(Long memberId);
}
