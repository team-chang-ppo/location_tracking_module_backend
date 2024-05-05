package org.changppo.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CardRepository cardRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Bean
    public Job AutomaticPaymentExecutionJob(Step executeAutomaticPaymentStep) {
        return new JobBuilder("AutomaticPaymentExecutionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeAutomaticPaymentStep)
                .build();
    }

    @Bean
    public Step executeAutomaticPaymentStep(RepositoryItemReader<Member> memberItemReaderForAutomaticPayment,
                                            ItemProcessor<Member, Payment> paymentProcessorForAutomaticPayment,
                                            ItemWriter<Payment> paymentItemWriterForAutomaticPayment) {
        return new StepBuilder("executeAutomaticPaymentStep", jobRepository)
                .<Member, Payment>chunk(10, transactionManager)
                .reader(memberItemReaderForAutomaticPayment)
                .processor(paymentProcessorForAutomaticPayment)
                .writer(paymentItemWriterForAutomaticPayment)
                .build();
    }

    @Bean
    public Job DeletionExecutionJob(Step executeDeletionStep) {
        return new JobBuilder("DeletionExecutionJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(executeDeletionStep)
                .build();
    }

    @Bean
    public Step executeDeletionStep(RepositoryItemReader<Member> memberItemReaderForDeletion,
                                            ItemProcessor<Member, Payment> paymentProcessorForDeletion,
                                            ItemWriter<Payment> paymentItemWriterForDeletion) {
        return new StepBuilder("executeDeletionStep", jobRepository)
                .<Member, Payment>chunk(10, transactionManager)
                .reader(memberItemReaderForDeletion)
                .processor(paymentProcessorForDeletion)
                .writer(paymentItemWriterForDeletion)
                .build();
    }

    @Bean
    public Job paymentExecutionJob(Step executePaymentStep) {
        return new JobBuilder("paymentExecutionJob", jobRepository)
                .start(executePaymentStep)
                .build();
    }

    @Bean
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
                        storePaymentExecutionDetails(contribution, paymentExecutionJobResponse);
                    }
                    catch (Exception e) {
                        contribution.getStepExecution().setStatus(BatchStatus.FAILED);
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
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

    private void storePaymentExecutionDetails(StepContribution contribution, PaymentExecutionJobResponse response) {
        ExecutionContext executionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
        executionContext.put("key", response.getKey());
        executionContext.put("cardType", response.getCardType());
        executionContext.put("cardIssuerCorporation", response.getCardIssuerCorporation());
        executionContext.put("cardBin", response.getCardBin());
    }

}
