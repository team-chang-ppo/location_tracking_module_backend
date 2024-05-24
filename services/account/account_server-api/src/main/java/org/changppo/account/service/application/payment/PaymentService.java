package org.changppo.account.service.application.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.payment.PaymentExecutionJobClient;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.service.event.payment.PaymentEventPublisher;
import org.changppo.account.type.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentEventPublisher paymentEventPublisher;
    private final PaymentExecutionJobClient paymentExecutionJobClient;
    private final ApiKeyRepository apiKeyRepository;

    @Transactional
    @PreAuthorize("@paymentAccessEvaluator.check(#id) and @paymentFailedStatusEvaluator.check(#id)")
    public PaymentDto repayment(@Param("id") Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        PaymentExecutionJobResponse paymentExecutionJobResponse = paymentExecutionJobClient.PaymentExecutionJob(createPaymentExecutionJobRequest(payment)).getData().orElseThrow(PaymentExecutionFailureException::new);
        updatePaymentStatus(payment, paymentExecutionJobResponse);
        handlePaymentComplete(payment.getMember());
        paymentEventPublisher.publishEvent(payment);
        return new PaymentDto(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getStartedAt(), payment.getEndedAt(), payment.getCardInfo(), payment.getCreatedAt());
    }

    private PaymentExecutionJobRequest createPaymentExecutionJobRequest(Payment payment) {
        return new PaymentExecutionJobRequest(
                payment.getMember().getId(),
                payment.getAmount(),
                payment.getEndedAt()
        );
    }

    private void updatePaymentStatus(Payment payment, PaymentExecutionJobResponse paymentExecutionJobResponse) {
        payment.changeStatus(PaymentStatus.COMPLETED_PAID, paymentExecutionJobResponse.getKey(), new PaymentCardInfo(paymentExecutionJobResponse.getCardType(), paymentExecutionJobResponse.getCardIssuerCorporation(), paymentExecutionJobResponse.getCardBin()));
    }

    public void handlePaymentComplete(Member member) {
        member.unbanForPaymentFailure();
        apiKeyRepository.unbanApiKeysForPaymentFailure(member.getId());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public PaymentListDto readAll(@Param("memberId")Long memberId, PaymentReadAllRequest req){
        Slice<PaymentDto> slice = paymentRepository.findAllDtosByMemberIdAndStatusNotCompletedFree(memberId, req.getLastPaymentId(), Pageable.ofSize(req.getSize()));
        return new PaymentListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    public Page<PaymentDto> readList(Pageable pageable) {
        return paymentRepository.findAllDtos(pageable);
    }
}
