package org.changppo.account.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.DatabaseCleaner;
import org.changppo.account.TestInitDB;
import org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.member.Member;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.repository.apikey.ApiKeyRepository;
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
import java.time.LocalDateTime;

import static org.changppo.account.batch.job.JobConfig.DELETION_JOB;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.oauth2.kakao.KakaoConstants.KAKAO_UNLINK_URL;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_PAYMENT_URL;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_SUBSCRIPTION_INACTIVE_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(value = "test")
@SpringBatchTest
@SpringBootTest
public class DeletionExecutionJobTest { //TODO. 비용집계 서버와 통신 이후 Mock으로 대체

    @Autowired
    JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    @Qualifier(DELETION_JOB)
    Job deletionExecutionJob;
    @Autowired
    DatabaseCleaner databaseCleaner;
    @Autowired
    TestInitDB testInitDB;
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
    Member requestDeletionMember;

    @BeforeEach
    void beforeEach() {
        jobLauncherTestUtils.setJob(deletionExecutionJob);
        databaseCleaner.clean();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        setupMembers();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        requestDeletionMember = memberRepository.findByName(testInitDB.getRequestDeletionMemberName()).orElseThrow();
    }

    @Test
    public void deletionExecutionJobPaymentSuccessTest() throws Exception {
        // given
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(requestDeletionMember.getId(), LocalDateTime.now());
        simulatePaymentSuccess(kakaopayApproveResponse);
        simulateCardInactiveSuccess();
        simulateMemberUnlinkSuccess();
        JobParameters jobParameters = buildJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        // then
        mockServer.verify();
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(1, stepExecution.getReadCount());
        assertEquals(1, stepExecution.getWriteCount());
        assertEquals(1, stepExecution.getCommitCount());
        assertTrue(memberRepository.findByName(testInitDB.getRequestDeletionMemberName()).isEmpty());
        assertTrue(apiKeyRepository.findByValue(testInitDB.getRequestDeletionApiKeyValue()).isEmpty());
        assertTrue(cardRepository.findByKey(testInitDB.getKakaopayCardByRequestDeletionMemberKey()).isEmpty());
    }

    @Test
    public void deletionExecutionJobPaymentFailTest() throws Exception {
        // given
        simulatePaymentFailure();
        JobParameters jobParameters = buildJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        Member updatedMember = memberRepository.findByName(testInitDB.getRequestDeletionMemberName()).orElseThrow();
        ApiKey updatedApiKey = apiKeyRepository.findByValue(testInitDB.getRequestDeletionApiKeyValue()).orElseThrow();

        // then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertTrue(updatedMember.isPaymentFailureBanned());
        assertTrue(updatedApiKey.isPaymentFailureBanned());
        assertFalse(updatedMember.isDeletionRequested());
        assertFalse(updatedApiKey.isDeletionRequested());
    }

    private void simulatePaymentSuccess(KakaopayApproveResponse kakaopayApproveResponse) throws Exception {
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
    }

    private void simulateMemberUnlinkSuccess() {
        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());
    }

    private void simulateCardInactiveSuccess() throws Exception {
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));
    }

    private void simulatePaymentFailure() {
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
    }

    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addLocalDateTime("JobStartTime", LocalDateTime.now().plusDays(3))
                .toJobParameters();
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
