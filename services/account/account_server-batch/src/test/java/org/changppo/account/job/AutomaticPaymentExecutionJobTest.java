package org.changppo.account.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.DatabaseCleaner;
import org.changppo.account.TestInitDB;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.type.PaymentStatus;
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
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.TemporalAdjusters;

import static org.changppo.account.batch.job.JobConfig.AUTOMATIC_PAYMENT_JOB;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_PAYMENT_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(value = "test")
@SpringBatchTest
@SpringBootTest
public class AutomaticPaymentExecutionJobTest { //TODO. 비용집계 서버와 통신 이후 Mock으로 대체

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    @Qualifier(AUTOMATIC_PAYMENT_JOB)
    Job automaticPaymentExecutionJob;
    @Autowired
    DatabaseCleaner databaseCleaner;
    @Autowired
    TestInitDB testInitDB;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    ApiKeyRepository apiKeyRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;

    MockRestServiceServer mockServer;
    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember;
    Card kakaopayCardByNormalMember;

    @BeforeEach
    void beforeEach() {
        jobLauncherTestUtils.setJob(automaticPaymentExecutionJob);
        databaseCleaner.clean();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        testInitDB.initPayment();
        setupMembers();
        setupCards();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        freeMember =  memberRepository.findByName(testInitDB.getFreeMemberName()).orElseThrow();
        normalMember = memberRepository.findByName(testInitDB.getNormalMemberName()).orElseThrow();
    }

    private void setupCards() {
        kakaopayCardByNormalMember = cardRepository.findByKey(testInitDB.getKakaopayCardByNormalMemberKey()).orElseThrow();
    }

    @Test
    public void automaticPaymentExecutionJobTest() throws Exception {
        // given
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(normalMember.getId(), LocalDateTime.now());
        simulatePaymentSuccess(kakaopayApproveResponse);
        JobParameters jobParameters = buildJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Payment paymentsByNormalMember = paymentRepository.findTopByMemberIdOrderByEndedAtDesc(normalMember.getId()).orElseThrow();
        Payment paymentsByFreeMember = paymentRepository.findTopByMemberIdOrderByEndedAtDesc(freeMember.getId()).orElseThrow();
        Member updatedFreeMember = memberRepository.findByName(testInitDB.getFreeMemberName()).orElseThrow();
        ApiKey updatedFreeApiKey = apiKeyRepository.findByValue(testInitDB.getFreeApiKeyValue()).orElseThrow();
        // then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(2, stepExecution.getReadCount());
        assertEquals(2, stepExecution.getWriteCount());
        assertEquals(1, stepExecution.getCommitCount());
        assertEquals(PaymentStatus.COMPLETED_PAID, paymentsByNormalMember.getStatus());
        assertEquals(kakaopayApproveResponse.getKey(), paymentsByNormalMember.getKey());
        assertEquals(kakaopayCardByNormalMember.getType(), paymentsByNormalMember.getCardInfo().getType());
        assertEquals(kakaopayCardByNormalMember.getIssuerCorporation(), paymentsByNormalMember.getCardInfo().getIssuerCorporation());
        assertEquals(kakaopayCardByNormalMember.getBin(), paymentsByNormalMember.getCardInfo().getBin());

        assertEquals(PaymentStatus.FAILED, paymentsByFreeMember.getStatus());
        assertTrue(updatedFreeMember.isPaymentFailureBanned());
        assertTrue(updatedFreeApiKey.isPaymentFailureBanned());
    }

    private void simulatePaymentSuccess(KakaopayApproveResponse kakaopayApproveResponse) throws Exception{
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
    }

    private JobParameters buildJobParameters() {
        LocalDateTime lastSunday = calculateLastSunday(LocalDateTime.now());
        LocalDateTime jobStartTime = lastSunday.plusDays(3);

        return new JobParametersBuilder()
                .addLocalDateTime("JobStartTime", jobStartTime)
                .toJobParameters();
    }

    private LocalDateTime calculateLastSunday(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY))
                .with(LocalTime.of(23, 59, 59));
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
