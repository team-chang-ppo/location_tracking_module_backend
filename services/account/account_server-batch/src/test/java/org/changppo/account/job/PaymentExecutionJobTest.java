package org.changppo.account.job;

import org.changppo.account.batch.job.JobConfig;
import org.changppo.account.entity.card.Card;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.card.CardRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@SpringBatchTest
@SpringBootTest(classes={JobConfig.class})
public class PaymentExecutionJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Mock
    private CardRepository cardRepository;

    @Mock
    private PaymentGatewayClient paymentGatewayClient;

    private AutoCloseable closeable;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        //jobLauncherTestUtils.getJobLauncher().setJobRepository(jobLauncherTestUtils.getJobRepository());
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testPaymentExecutionJobSuccess() throws Exception {
        Card card = new Card();
        when(cardRepository.findAllCardByMemberId(any(Long.class))).thenReturn(Arrays.asList(card));
        when(paymentGatewayClient.supports(any())).thenReturn(true);
        when(paymentGatewayClient.payment(any())).thenReturn(Optional.of(...));

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("memberId", 12345L, true)
                .addString("amount", "150.00", true)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(JobConfig.PAYMENT_STEP, jobParameters);

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    public void testPaymentExecutionJobFailure() throws Exception {
        when(cardRepository.findAllCardByMemberId(any(Long.class))).thenThrow(new RuntimeException("Database error"));

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("memberId", 12345L, true)
                .addString("amount", "150.00", true)
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchStep(JobConfig.PAYMENT_STEP, jobParameters);

        assertEquals(BatchStatus.FAILED, jobExecution.getStatus());
    }
}
