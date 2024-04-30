package org.changppo.cost_management_service.repository.payment;

import org.changppo.cost_management_service.dto.payment.PaymentDto;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    void deleteAllByMemberId(Long memberId);
    Optional<Payment> findFirstByMemberIdOrderByEndedAtDesc(Long memberId);
    @Query("select new org.changppo.cost_management_service.dto.payment.PaymentDto(p.id, p.amount, p.status, p.startedAt, p.endedAt, p.cardInfo, p.createdAt)" +
            "from Payment p where p.member.id = :memberId and p.id <= :lastPaymentId " +
            "order by p.id desc")
    Slice<PaymentDto> findAllByMemberIdOrderByDesc(@Param("memberId") Long memberId, @Param("lastPaymentId")Long lastPaymentId, Pageable pageable);
}
