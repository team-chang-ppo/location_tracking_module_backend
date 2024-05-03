package org.changppo.account.batch.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.payment.FakePaymentInfoClient;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class ProcessorConfig {

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;
    private final FakePaymentInfoClient fakePaymentInfoClient;
    private final PaymentRepository paymentRepository;

    @Bean
    public ItemProcessor<Member, Payment> paymentProcessor() {
        return member -> {
            LocalDateTime periodStart = paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId())
                    .map(Payment::getEndedAt)
                    .orElse(member.getCreatedAt());
            LocalDateTime periodEnd = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).withHour(0).withMinute(0).withSecond(0).withNano(0);
            BigDecimal paymentAmount =  fakePaymentInfoClient.getPaymentAmountForPeriod(member.getId(), periodStart, periodEnd).getData().orElseThrow(()-> new RuntimeException("Payment amount is not found"));
            if (paymentAmount.compareTo(BigDecimal.valueOf(100.0)) <= 0){
                return createCompletedPayment(member, paymentAmount, null, periodStart, periodEnd);
            } else {
                return createPaymentDecision(member, paymentAmount, periodStart, periodEnd);
            }
        };
    }

    private Payment createPaymentDecision(Member member, BigDecimal paymentAmount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        if (member.getMemberRoles().stream().anyMatch(role -> role.getRole().getRoleType() == RoleType.ROLE_FREE)) {
            return createFailedPayment(member, paymentAmount, periodStart, periodEnd);
        } else {
            return executePayment(member, paymentAmount, periodStart, periodEnd);
        }
    }

    public Payment executePayment(Member member, BigDecimal paymentAmount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("memberId", member.getId())
                .addString("amount", paymentAmount.toString())
                .addLocalDateTime("date", periodEnd)
                .toJobParameters();
        JobExecution jobExecution = jobRepository.getLastJobExecution(paymentExecutionJob.getName(), jobParameters);
        if (jobExecution == null) {
            try {
                jobExecution = jobLauncher.run(paymentExecutionJob, jobParameters);
            } catch (Exception e) {
                log.error("Payment execution failed", e);
            }
        }
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            PaymentCardInfo cardInfo = (PaymentCardInfo) jobExecution.getExecutionContext().get("paymentCardInfo");
            return createCompletedPayment(member, paymentAmount, cardInfo, periodStart, periodEnd);
        }
        return createFailedPayment(member, paymentAmount, periodStart, periodEnd);
    }

    private Payment createFailedPayment(Member member, BigDecimal amount, LocalDateTime periodStart, LocalDateTime periodEnd) {
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.FAILED)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .cardInfo(null)
                .build();
    }

    private Payment createCompletedPayment(Member member, BigDecimal amount, PaymentCardInfo paymentCardInfo, LocalDateTime periodStart, LocalDateTime periodEnd) {
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

