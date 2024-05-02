package org.changppo.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayPaymentRequest;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.response.ClientResponse;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
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
    public Step executeAutomaticPaymentStep(RepositoryItemReader<Member> memberItemReader,
                                            ItemProcessor<Member, Payment> paymentProcessor,
                                            ItemWriter<Payment> paymentWriter) {
        return new StepBuilder("executeAutomaticPaymentStep", jobRepository)
                .<Member, Payment>chunk(10, transactionManager)
                .reader(memberItemReader)
                .processor(paymentProcessor)
                .writer(paymentWriter)
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
                        PaymentCardInfo paymentCardInfo = cards.stream()
                                .map(card -> ProcessPayment(card, memberId, new BigDecimal(amount)))
                                .filter(Objects::nonNull)
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("All payment attempts failed for memberId: " + memberId));
                        // TODO. 테스트용 로직 제거
                        // PaymentCardInfo paymentCardInfo = new PaymentCardInfo("card.getType()", "card.getIssuerCorporation()", "card.getBin()");
                        contribution.getStepExecution().getJobExecution().getExecutionContext().put("paymentCardInfo", paymentCardInfo);
                    }
                    catch (Exception e) {
                        contribution.getStepExecution().setStatus(BatchStatus.FAILED);
                        contribution.setExitStatus(ExitStatus.FAILED);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    private PaymentCardInfo ProcessPayment(Card card, Long memberId, BigDecimal amount) {
        KakaopayPaymentRequest request = new KakaopayPaymentRequest(
                card.getKey(),
                UUID.randomUUID().toString(),
                memberId,
                "위치 추적 모듈 정기 결제",
                1,
                amount.intValue(),
                0
        );
        return paymentGatewayClients.stream()
                .filter(client -> client.supports(card.getPaymentGateway().getPaymentGatewayType()))
                .findFirst()
                .map(client -> client.payment(request))
                .flatMap(ClientResponse::getData)
                .map(Data -> new PaymentCardInfo(card.getType(), card.getIssuerCorporation(), card.getBin()))
                .orElse(null);
    }
}
