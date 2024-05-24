package org.changppo.account.service.domain.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.payment.PaymentListDto;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.payment.PaymentExecutionJobClient;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.type.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class PaymentDomainService {

    private final PaymentRepository paymentRepository;
    private final PaymentExecutionJobClient paymentExecutionJobClient;

    @Transactional(propagation = Propagation.MANDATORY)
    public Payment processRepayment(Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        PaymentExecutionJobResponse paymentExecutionJobResponse = paymentExecutionJobClient.PaymentExecutionJob(createPaymentExecutionJobRequest(payment)).getData().orElseThrow(PaymentExecutionFailureException::new);
        payment.changeStatus(PaymentStatus.COMPLETED_PAID, paymentExecutionJobResponse.getKey(), new PaymentCardInfo(paymentExecutionJobResponse.getCardType(), paymentExecutionJobResponse.getCardIssuerCorporation(), paymentExecutionJobResponse.getCardBin()));
        return payment;
    }

    private PaymentExecutionJobRequest createPaymentExecutionJobRequest(Payment payment) {
        return new PaymentExecutionJobRequest(
                payment.getMember().getId(),
                payment.getAmount(),
                payment.getEndedAt()
        );
    }

    public PaymentListDto getPaymentList(Long memberId, Long lastPaymentId, Pageable pageable) {
        Slice<PaymentDto> slice =  paymentRepository.findAllDtosByMemberIdAndStatusNotCompletedFree(memberId, lastPaymentId, pageable);
        return new PaymentListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }

    public Page<PaymentDto> getPaymentDtos(Pageable pageable) {
        return paymentRepository.findAllDtos(pageable);
    }
}
