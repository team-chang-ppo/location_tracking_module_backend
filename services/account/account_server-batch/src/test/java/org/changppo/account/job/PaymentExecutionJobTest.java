package org.changppo.account.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.DatabaseCleaner;
import org.changppo.account.TestInitDB;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.changppo.account.batch.job.JobConfig.PAYMENT_JOB;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_PAYMENT_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(value = "test")
@SpringBatchTest
@SpringBootTest
public class PaymentExecutionJobTest {

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    @Qualifier(PAYMENT_JOB)
    Job paymentExecutionJob;
    @Autowired
    DatabaseCleaner databaseCleaner;
    @Autowired
    TestInitDB testInitDB;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;

    MockRestServiceServer mockServer;
    ObjectMapper objectMapper = new ObjectMapper();
    Member normalMember;
    Card kakaopayCardByNormalMember;

    @BeforeEach
    void beforeEach() {
        jobLauncherTestUtils.setJob(paymentExecutionJob);
        databaseCleaner.clean();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initCard();
        setupMembers();
        setupCards();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        normalMember = memberRepository.findByName(testInitDB.getNormalMemberName()).orElseThrow();
    }

    private void setupCards() {
        kakaopayCardByNormalMember = cardRepository.findByKey(testInitDB.getKakaopayCardByNormalMemberKey()).orElseThrow();
    }
    
    @Test
    public void paymentExecutionJobTest() throws Exception {
        // given
        BigDecimal paymentAmount = new BigDecimal("10000");
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(normalMember.getId(), LocalDateTime.now());
        simulatePaymentSuccess(kakaopayApproveResponse);
        JobParameters jobParameters = buildJobParameters(normalMember.getId(), paymentAmount, LocalDateTime.now());
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        PaymentExecutionJobResponse paymentExecutionJobResponse = extractPaymentDetails(jobExecution);
        // then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(kakaopayApproveResponse.getKey(), paymentExecutionJobResponse.getKey());
        assertEquals(kakaopayCardByNormalMember.getType(), paymentExecutionJobResponse.getCardType());
        assertEquals(kakaopayCardByNormalMember.getIssuerCorporation(), paymentExecutionJobResponse.getCardIssuerCorporation());
        assertEquals(kakaopayCardByNormalMember.getBin(), paymentExecutionJobResponse.getCardBin());
    }

    private void simulatePaymentSuccess(KakaopayApproveResponse kakaopayApproveResponse) throws Exception{
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
    }

    private JobParameters buildJobParameters(Long memberId, BigDecimal amount, LocalDateTime date) {
        return new JobParametersBuilder()
                .addLong("memberId", memberId)
                .addString("amount", amount.toString())
                .addLocalDateTime("date", date)
                .toJobParameters();
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

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
