package org.changppo.account.repository.payment;

import org.changppo.account.entity.payment.Payment;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Optional;

public interface QuerydslPaymentRepository {
    void deleteAllByMemberId(Long memberId);
    Optional<Payment> findFirstByMemberIdOrderByEndedAtDesc(Long memberId);
    Slice<PaymentDto> findAllDtosByMemberIdAndStatusNotCompletedFree(Long memberId, Long lastPaymentId, Pageable pageable);
    Page<PaymentDto> findAllDtos(Pageable pageable);
}
