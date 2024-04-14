package org.changppo.cost_management_service.service.payment.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.payment.Payment;
import org.changppo.cost_management_service.entity.payment.PaymentCardInfo;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.service.paymentgateway.PaymentGatewayClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobConfig{

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CardRepository cardRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Bean
    public Job processPaymentJob(Step processPaymentStep) {
        return new JobBuilder("processPaymentJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(processPaymentStep)
                .build();
    }

    @Bean
    public Step processPaymentStep(RepositoryItemReader<Member> memberItemReader,
                                    ItemProcessor<Member, Payment> paymentProcessor,
                                    ItemWriter<Payment> paymentWriter) {
        return new StepBuilder("paymentStep", jobRepository)
                .<Member, Payment>chunk(10, transactionManager)
                .reader(memberItemReader)
                .processor(paymentProcessor)
                .writer(paymentWriter)
                .build();
    }

    @Bean
    public Job paymentJob(Step paymentStep) {
        return new JobBuilder("paymentProcessingJob", jobRepository)
                .start(paymentStep)
                .build();
    }

    @Bean
    @JobScope
    public Step paymentStep(@Value("#{jobParameters[memberId]}") Long memberId,
                            @Value("#{jobParameters[amount]}") Long amount) {
        return new StepBuilder("paymentStep", jobRepository)
                .tasklet((contribution, chunkContext) ->{
            if (memberId == null || amount == null) {
                throw new IllegalArgumentException("Member ID and amount must be provided");
            }
//            List<Card> cards = cardRepository.findAllCardByMemberId(memberId);
//            for (Card card : cards) {
//                try {
//                    PaymentGatewayClient client = paymentGatewayClients.stream()
//                            .filter(c -> c.supports(card.getPaymentGateway().getPaymentGatewayType()))
//                            .findFirst()
//                            .orElseThrow(() -> new IllegalStateException("Unsupported payment gateway"));
//                    KakaopayPaymentRequest request = new KakaopayPaymentRequest(
//                            card.getKey(),
//                            UUID.randomUUID().toString(),
//                            memberId,
//                            "위치 추적 모듈 정기 결제",
//                            1,
//                            amount.intValue(),
//                            0
//                    );
//                    client.payment(request);
//                    PaymentCardInfo paymentCardInfo = new PaymentCardInfo(card.getType(), card.getIssuerCorporation(), card.getBin());
//                    contribution.getStepExecution().getJobExecution().getExecutionContext().put("paymentCardInfo", paymentCardInfo);
//                    break;
//                } catch (PaymentGatewayBusinessException e) {
//                    log.info("Failed to process payment for card {}", card.getKey());
//                }
//            }
            PaymentCardInfo paymentCardInfo = new PaymentCardInfo("card.getType()", "card.getIssuerCorporation()", "card.getBin()");
            contribution.getStepExecution().getJobExecution().getExecutionContext().put("paymentCardInfo", paymentCardInfo);
            return RepeatStatus.FINISHED;
        }, transactionManager)
        .build();
    }
}
