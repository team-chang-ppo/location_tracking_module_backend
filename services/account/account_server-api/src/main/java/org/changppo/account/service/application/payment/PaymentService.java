package org.changppo.account.service.application.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.payment.PaymentDomainService;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentDomainService paymentDomainService;
    private final PaymentEventPublisher paymentEventPublisher;
    private final MemberDomainService memberDomainService;
    private final ApiKeyDomainService apiKeyDomainService;

    @Transactional
    @PreAuthorize("@paymentAccessEvaluator.check(#id) and @paymentFailedStatusEvaluator.check(#id)")
    public PaymentDto repayment(@Param("id") Long id) {
        Payment payment = paymentDomainService.processRepayment(id);
        memberDomainService.unbanMemberPaymentFailure(payment.getMember());
        apiKeyDomainService.unbanApiKeysForPaymentFailure(payment.getMember().getId());
        paymentEventPublisher.publishEvent(payment);
        return new PaymentDto(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getStartedAt(), payment.getEndedAt(), payment.getCardInfo(), payment.getCreatedAt());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public PaymentListDto readAll(@Param("memberId") Long memberId, PaymentReadAllRequest req) {
        return paymentDomainService.getPaymentList(memberId, req.getLastPaymentId(), Pageable.ofSize(req.getSize()));
    }

    public Page<PaymentDto> readList(Pageable pageable) {
        return paymentDomainService.getPaymentDtos(pageable);
    }
}
