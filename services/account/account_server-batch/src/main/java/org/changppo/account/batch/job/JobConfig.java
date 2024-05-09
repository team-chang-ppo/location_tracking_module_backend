package org.changppo.account.batch.job;

import lombok.extern.slf4j.Slf4j;
import org.changppo.account.batch.config.reader.QuerydslNoOffsetPagingItemReader;
import org.changppo.account.batch.config.reader.QuerydslZeroPagingItemReader;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.paymentgateway.dto.PaymentRequest;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayPaymentRequest;
import org.changppo.account.repository.card.CardRepository;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.changppo.account.batch.config.TransactionManagerConfig.DOMAIN_TRANSACTION_MANAGER;
import static org.changppo.account.batch.job.ProcessorConfig.AUTOMATIC_PAYMENT_PROCESSOR;
import static org.changppo.account.batch.job.ProcessorConfig.DELETION_PROCESSOR;
import static org.changppo.account.batch.job.ReaderConfig.AUTOMATIC_PAYMENT_READER;
import static org.changppo.account.batch.job.ReaderConfig.DELETION_READER;
import static org.changppo.account.batch.job.WriterConfig.AUTOMATIC_PAYMENT_WRITER;
import static org.changppo.account.batch.job.WriterConfig.DELETION_WRITER;

@Configuration
@Slf4j
public class JobConfig {

    public static final String AUTOMATIC_PAYMENT_JOB = "AutomaticPaymentExecutionJob";
    public static final String AUTOMATIC_PAYMENT_STEP = "executeAutomaticPaymentStep";
    public static final String DELETION_JOB = "DeletionExecutionJob";
    public static final String DELETION_STEP = "executeDeletionStep";
    public static final String PAYMENT_JOB = "paymentExecutionJob";
    public static final String PAYMENT_STEP = "executePaymentStep";

    private final JobRepository jobRepository;
    private final PlatformTransactionManager domainTransactionManager;
    private final CardRepository cardRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    public JobConfig(JobRepository jobRepository, @Qualifier(DOMAIN_TRANSACTION_MANAGER) PlatformTransactionManager domainTransactionManager, CardRepository cardRepository, List<PaymentGatewayClient> paymentGatewayClients) {
        this.jobRepository = jobRepository;
        this.domainTransactionManager = domainTransactionManager;
        this.cardRepository = cardRepository;
        this.paymentGatewayClients = paymentGatewayClients;
    }

    @Bean(AUTOMATIC_PAYMENT_JOB)
    public Job AutomaticPaymentExecutionJob(@Qualifier(AUTOMATIC_PAYMENT_STEP) Step executeAutomaticPaymentStep) {
        return new JobBuilder("AutomaticPaymentExecutionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeAutomaticPaymentStep)
                .build();
    }

    @Bean(AUTOMATIC_PAYMENT_STEP)
    public Step executeAutomaticPaymentStep(@Qualifier(AUTOMATIC_PAYMENT_READER) QuerydslNoOffsetPagingItemReader<Member> memberItemReaderForAutomaticPayment,
                                            @Qualifier(AUTOMATIC_PAYMENT_PROCESSOR) ItemProcessor<Member, Payment> paymentProcessorForAutomaticPayment,
                                            @Qualifier(AUTOMATIC_PAYMENT_WRITER) ItemWriter<Payment> paymentItemWriterForAutomaticPayment) {
        return new StepBuilder("executeAutomaticPaymentStep", jobRepository)
                .<Member, Payment>chunk(10, domainTransactionManager)
                .reader(memberItemReaderForAutomaticPayment)
                .processor(paymentProcessorForAutomaticPayment)
                .writer(paymentItemWriterForAutomaticPayment)
                .build();
    }

    @Bean(DELETION_JOB)
    public Job DeletionExecutionJob(@Qualifier(DELETION_STEP) Step executeDeletionStep) {
        return new JobBuilder("DeletionExecutionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeDeletionStep)
                .build();
    }

    @Bean(DELETION_STEP)
    public Step executeDeletionStep(@Qualifier(DELETION_READER) QuerydslZeroPagingItemReader<Member> memberItemReaderForDeletion,
                                    @Qualifier(DELETION_PROCESSOR) ItemProcessor<Member, Payment> paymentProcessorForDeletion,
                                    @Qualifier(DELETION_WRITER) ItemWriter<Payment> paymentItemWriterForDeletion) {
        return new StepBuilder("executeDeletionStep", jobRepository)
                .<Member, Payment>chunk(10, domainTransactionManager)
                .reader(memberItemReaderForDeletion)
                .processor(paymentProcessorForDeletion)
                .writer(paymentItemWriterForDeletion)
                .build();
    }

    @Bean(PAYMENT_JOB)
    public Job paymentExecutionJob(@Qualifier(PAYMENT_STEP) Step executePaymentStep) {
        return new JobBuilder("paymentExecutionJob", jobRepository)
                .start(executePaymentStep)
                .build();
    }

    @Bean(PAYMENT_STEP)
    @JobScope
    public Step executePaymentStep(@Value("#{jobParameters[memberId]}") Long memberId,
                            @Value("#{jobParameters[amount]}") String amount) {
        return new StepBuilder("executePaymentStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    try {
                        List<Card> cards = cardRepository.findAllCardByMemberId(memberId);
                        PaymentExecutionJobResponse paymentExecutionJobResponse = cards.stream()
                                .map(card -> processPayment(card, memberId, new BigDecimal(amount)))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("All payment attempts failed for memberId: " + memberId));  //TODO. 메서드로 분리
                        savePaymentExecutionDetails(contribution, paymentExecutionJobResponse);
                    }
                    catch (Exception e) {
                        contribution.getStepExecution().setStatus(BatchStatus.FAILED);
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }
                    return RepeatStatus.FINISHED;
                }, domainTransactionManager)
                .build();
    }

    private PaymentExecutionJobResponse processPayment(Card card, Long memberId, BigDecimal amount) {
        PaymentRequest request = createPaymentRequest(card, memberId, amount);
        return paymentGatewayClients.stream()
                .filter(client -> client.supports(card.getPaymentGateway().getPaymentGatewayType()))
                .findFirst()
                .flatMap(client -> client.payment(request).getData())
                .map(response -> new PaymentExecutionJobResponse(
                        response.getKey(),
                        card.getType(),
                        card.getIssuerCorporation(),
                        card.getBin()
                ))
                .orElse(null);
    }

    private PaymentRequest createPaymentRequest(Card card, Long memberId, BigDecimal amount) {
        return switch (card.getPaymentGateway().getPaymentGatewayType()) {
            case PG_KAKAOPAY -> new KakaopayPaymentRequest(
                    card.getKey(),
                    UUID.randomUUID().toString(),
                    memberId,
                    "위치 추적 모듈 정기 결제",
                    1,
                    amount.intValue(),
                    0
            );
            default -> throw new IllegalArgumentException("Unsupported payment gateway type: " + card.getPaymentGateway().getPaymentGatewayType());
        };
    }

    private void savePaymentExecutionDetails(StepContribution contribution, PaymentExecutionJobResponse response) {
        ExecutionContext executionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        safePutString(executionContext, "key", response.getKey());
        safePutString(executionContext, "cardType", response.getCardType());
        safePutString(executionContext, "cardIssuerCorporation", response.getCardIssuerCorporation());
        safePutString(executionContext, "cardBin", response.getCardBin());
    }

    private void safePutString(ExecutionContext executionContext, String key, Object value) {
        executionContext.put(key, Objects.requireNonNullElse(value, "Unknown"));
    }

}
