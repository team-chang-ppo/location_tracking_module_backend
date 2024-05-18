package org.changppo.account.repository.payment;

import org.changppo.account.entity.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> , QuerydslPaymentRepository{
    Optional<Payment> findByKey(String key);
    long countByMemberId(Long memberId);
    Optional<Payment> findTopByMemberIdOrderByEndedAtDesc(Long memberId);
}
