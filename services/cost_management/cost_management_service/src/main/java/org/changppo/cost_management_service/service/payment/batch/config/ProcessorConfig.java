package org.changppo.cost_management_service.service.payment.batch.config;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.entity.payment.PaymentStatus;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.payment.PaymentRepository;
import org.changppo.cost_management_service.service.payment.batch.fake.FakePaymentInfoClient;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Configuration
@RequiredArgsConstructor
public class ProcessorConfig {  // TODO. ClassifierCompositeItemProcessor 사용해서 Refactoring

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentJob;
    private final FakePaymentInfoClient fakePaymentInfoClient;
    private final ApiKeyRepository apiKeyRepository;
    private final PaymentRepository paymentRepository;

    @Bean
    public ItemProcessor<Member, Payment> paymentProcessor() {
        return member -> {
            LocalDateTime periodStart = paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId())
                    .map(Payment::getEndedAt)
                    .orElse(member.getCreatedAt());
            LocalDateTime periodEnd = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
            int paymentAmount =  fakePaymentInfoClient.getPaymentAmountForPeriod(member.getId(), periodStart, periodEnd);
            if (paymentAmount <= 100) {
                return createCompletedFreePayment(member, paymentAmount, periodStart, periodEnd);
            } else {
                return createPaymentDecision(member, paymentAmount, periodStart, periodEnd);
            }
        };
    }

    private Payment createPaymentDecision(Member member, int amount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (member.getMemberRoles().stream()
                .anyMatch(role -> role.getRole().getRoleType() == RoleType.ROLE_FREE)) {
            return createFailedPayment(member, amount, periodStart, periodEnd);
        } else {
            return executePayment(member, amount, periodStart, periodEnd);
        }
    }

    public Payment executePayment(Member member, int amount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("memberId", member.getId())
                .addLong("amount", (long) amount)
                .addLocalDateTime("date", periodEnd)
                .toJobParameters();
        try {
            JobExecution jobExecution = jobLauncher.run(paymentJob, jobParameters);
            PaymentCardInfo cardInfo = (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
            if (cardInfo == null) {
                throw new IllegalStateException("Card information could not be retrieved from the execution context.");
            }
            return jobExecution.getStatus() == BatchStatus.COMPLETED ? createCompletedPaidPayment(member, amount, cardInfo, periodStart, periodEnd) : createFailedPayment(member, amount, periodStart, periodEnd);
        } catch (JobRestartException | JobExecutionAlreadyRunningException | JobInstanceAlreadyCompleteException e) {
            JobExecution jobExecution = jobRepository.getLastJobExecution(paymentJob.getName(), jobParameters);
            PaymentCardInfo cardInfo = (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
            return createCompletedPaidPayment(member, amount, cardInfo, periodStart, periodEnd);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute payment job due to critical error", e);
        }
    }

    private Payment createCompletedFreePayment(Member member, int amount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.COMPLETED_FREE)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .cardInfo(null)
                .build();
    }

    private Payment createFailedPayment(Member member, int amount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        handlePaymentFailure(member);
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.FAILED)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .cardInfo(null)
                .build();
    }

    private void handlePaymentFailure(Member member) {
        member.banForPaymentFailure(LocalDateTime.now());
        apiKeyRepository.banApiKeysForPaymentFailure(member.getId(), LocalDateTime.now());
    }

    private Payment createCompletedPaidPayment(Member member, int amount, PaymentCardInfo paymentCardInfo, LocalDateTime periodStart, LocalDateTime periodEnd) {
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.COMPLETED_PAID)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .cardInfo(paymentCardInfo)
                .build();
    }
}

