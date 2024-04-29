package org.changppo.cost_management_service.service.payment;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.payment.PaymentDto;
import org.changppo.cost_management_service.dto.payment.PaymentListDto;
import org.changppo.cost_management_service.dto.payment.PaymentReadAllRequest;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.changppo.cost_management_service.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.cost_management_service.response.exception.payment.PaymentExecutionNotFoundException;
import org.changppo.cost_management_service.response.exception.payment.PaymentNotFoundException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;
    private final ApplicationEventPublisher publisher;

    @Transactional
    @PreAuthorize("@paymentAccessEvaluator.check(#id) and !@memberPaymentFailureStatusEvaluator.check(#id)")
    public PaymentDto repayment(@Param("id") Long id) {
        Payment payment = paymentRepository.findById(id).orElseThrow(PaymentNotFoundException::new);
        JobParameters jobParameters = createJobParameters(payment);
        JobExecution jobExecution = createJobExecution(jobParameters); //TODO. 추후 멀티모듈 적용시 의존성 분리
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            updatePaymentStatus(payment, jobExecution);
        }
        payment.publishCreatedEvent(publisher);
        return new PaymentDto(payment.getId(), payment.getAmount(), payment.getStatus(), payment.getStartedAt(), payment.getEndedAt(), payment.getCardInfo(), payment.getCreatedAt());
    }

    private JobParameters createJobParameters(Payment payment) {
        return new JobParametersBuilder()
                .addLong("memberId", payment.getMember().getId())
                .addLong("amount", (long) payment.getAmount())
                .addLocalDateTime("date", payment.getEndedAt())
                .toJobParameters();
    }

    private JobExecution createJobExecution(JobParameters jobParameters) {
        return Optional.ofNullable(jobRepository.getLastJobExecution(paymentExecutionJob.getName(), jobParameters))
                .map(jobExecution -> {
                    if (jobExecution.getStatus() == BatchStatus.FAILED) {
                        try {
                            return jobLauncher.run(paymentExecutionJob, jobParameters);
                        } catch (Exception e) {
                            throw new PaymentExecutionFailureException(e);
                        }
                    }
                    return jobExecution;
                })
                .orElseThrow(PaymentExecutionNotFoundException::new);
    }

    private void updatePaymentStatus(Payment payment, JobExecution jobExecution) {
        PaymentCardInfo cardInfo = (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
        payment.changeStatus(PaymentStatus.COMPLETED_PAID, cardInfo);
    }

    @PreAuthorize("@paymentAccessEvaluator.check(#memberId)")
    public PaymentListDto readAll(@Param("memberId")Long memberId, PaymentReadAllRequest req){
        Slice<PaymentDto> slice = paymentRepository.findAllByMemberIdOrderByDesc(memberId, req.getLastPaymentId(), Pageable.ofSize(req.getSize()));
        return new PaymentListDto(slice.getNumberOfElements(), slice.hasNext(), slice.getContent());
    }
}