package org.changppo.account.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.billing.BillingInfoClient;
import org.changppo.account.batch.dto.PaymentExecutionJobResponse;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.type.PaymentStatus;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.changppo.account.batch.job.JobConfig.PAYMENT_JOB;

@Configuration
@Slf4j
public class ProcessorConfig {

    public static final String AUTOMATIC_PAYMENT_PROCESSOR = "paymentProcessorForAutomaticPayment";
    public static final String DELETION_PROCESSOR = "paymentProcessorForDeletion";

    private final JobLauncher jobLauncher;
    private final JobRepository jobRepository;
    private final Job paymentExecutionJob;
    private final BillingInfoClient billingInfoClient;
    private final PaymentRepository paymentRepository;

    public ProcessorConfig(JobLauncher jobLauncher, JobRepository jobRepository, @Qualifier(PAYMENT_JOB) Job paymentExecutionJob, BillingInfoClient billingInfoClient, PaymentRepository paymentRepository) {
        this.jobLauncher = jobLauncher;
        this.jobRepository = jobRepository;
        this.paymentExecutionJob = paymentExecutionJob;
        this.billingInfoClient = billingInfoClient;
        this.paymentRepository = paymentRepository;
    }

    @Bean(AUTOMATIC_PAYMENT_PROCESSOR)
    @StepScope
    public ItemProcessor<Member, Payment> paymentProcessorForAutomaticPayment(@Value("#{jobParameters[JobStartTime]}") LocalDateTime jobStartTime) {
        return member -> {
            LocalDate lastSunday = calculateLastSunday(jobStartTime.toLocalDate());
            if (lastSunday.isBefore(jobStartTime.toLocalDate().minusDays(2))) {  //결제 정합성을 고려하여 이틀 이전의 시간으로 조회 TODO. 추후 개선
                return processPaymentForMember(member, lastSunday);
            }
            return null;
        };
    }

    private LocalDate calculateLastSunday(LocalDate date) {  //시범 운영에서는 일주일 단위로 계산
        return date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    }

    @Bean(DELETION_PROCESSOR)
    public ItemProcessor<Member, Payment> paymentProcessorForDeletion() {
        return member -> processPaymentForMember(member, member.getDeletionRequestedAt().toLocalDate());
    }

    private Payment processPaymentForMember(Member member, LocalDate periodEnd) {
        LocalDate periodStart = paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId())
                .map(payment -> payment.getEndedAt().plusDays(1))
                .orElse(member.getCreatedAt().toLocalDate());

        if (!periodStart.isAfter(periodEnd)) {
            BigDecimal paymentAmount = billingInfoClient.getBillingAmountForPeriod(member.getId(), periodStart, periodEnd).getData()
                    .orElseThrow(() -> new RuntimeException("Payment amount is not found")).getResult().getTotalCost();
            return decidePaymentExecution(member, paymentAmount, periodStart, periodEnd);
        }
        return null;
    }

    private Payment decidePaymentExecution(Member member, BigDecimal amount, LocalDate periodStart, LocalDate periodEnd) {
        if (amount.compareTo(BigDecimal.valueOf(100.0)) <= 0) {
            return createCompletedFreePayment(member, amount, periodStart, periodEnd);
        } else {
            return executePayment(member, amount, periodStart, periodEnd);
        }
    }

    private Payment executePayment(Member member, BigDecimal amount, LocalDate periodStart, LocalDate periodEnd) {
        JobParameters jobParameters = buildJobParameters(member.getId(), amount, periodEnd);
        JobExecution jobExecution = createJobExecution(jobParameters);
        return processJobExecution(jobExecution, member, amount, periodStart, periodEnd);
    }

    private JobParameters buildJobParameters(Long memberId, BigDecimal amount, LocalDate periodEnd) {
        return new JobParametersBuilder()
                .addLong("memberId", memberId)
                .addString("amount", amount.toString())
                .addLocalDate("date", periodEnd)
                .toJobParameters();
    }

    private JobExecution createJobExecution(JobParameters jobParameters) {
        JobExecution jobExecution = jobRepository.getLastJobExecution(paymentExecutionJob.getName(), jobParameters);
        if (jobExecution == null) {
            try {
                jobExecution = jobLauncher.run(paymentExecutionJob, jobParameters);
            } catch (Exception e) {
                log.error("Payment execution failed", e);
            }
        }
        return jobExecution;
    }

    private Payment processJobExecution(JobExecution jobExecution, Member member, BigDecimal amount, LocalDate periodStart, LocalDate periodEnd) {
        if (jobExecution != null && jobExecution.getStatus() == BatchStatus.COMPLETED) {
            PaymentExecutionJobResponse paymentExecutionJobResponse = extractPaymentDetails(jobExecution);
            return createCompletedPaidPayment(member, paymentExecutionJobResponse.getKey(), amount,
                    new PaymentCardInfo(paymentExecutionJobResponse.getCardType(), paymentExecutionJobResponse. getCardIssuerCorporation(), paymentExecutionJobResponse.getCardBin()), periodStart, periodEnd);
        }
        return createFailedPayment(member, amount, periodStart, periodEnd);
    }

    private PaymentExecutionJobResponse extractPaymentDetails(JobExecution jobExecution) {
        String key = safeExtractString(jobExecution, "key");
        String cardType = safeExtractString(jobExecution, "cardType");
        String cardIssuerCorporation = safeExtractString(jobExecution, "cardIssuerCorporation");
        String cardBin = safeExtractString(jobExecution, "cardBin");
        return new PaymentExecutionJobResponse(key, cardType, cardIssuerCorporation, cardBin);
    }

    private String safeExtractString(JobExecution jobExecution, String key) {
        Object value = jobExecution.getExecutionContext().get(key);
        return value != null ? value.toString() : "Unknown";
    }

    private Payment createFailedPayment(Member member, BigDecimal amount, LocalDate periodStart, LocalDate periodEnd) {
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.FAILED)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .build();
    }

    private Payment createCompletedFreePayment(Member member, BigDecimal amount, LocalDate periodStart, LocalDate periodEnd) {
        return Payment.builder()
                .amount(amount)
                .status(PaymentStatus.COMPLETED_FREE)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .build();
    }

    private Payment createCompletedPaidPayment(Member member, String key, BigDecimal amount, PaymentCardInfo paymentCardInfo, LocalDate periodStart, LocalDate periodEnd) {
        return Payment.builder()
                .key(key)
                .amount(amount)
                .status(PaymentStatus.COMPLETED_PAID)
                .startedAt(periodStart)
                .endedAt(periodEnd)
                .member(member)
                .cardInfo(paymentCardInfo)
                .build();
    }
}
