package org.changppo.account.job;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.DatabaseCleaner;
import org.changppo.account.TestInitDB;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.payment.BillingInfoProperties;
import org.changppo.account.payment.dto.BillingInfoResponse;
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
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

import static org.changppo.account.batch.job.JobConfig.AUTOMATIC_PAYMENT_JOB;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.payment.BillingInfoClient.BILLING_INFO_URL_TEMPLATE;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_PAYMENT_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ActiveProfiles(value = "test")
@SpringBatchTest
@SpringBootTest
@EnableConfigurationProperties(BillingInfoProperties.class)
public class AutomaticPaymentExecutionJobTest {

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
    BillingInfoProperties billingInfoProperties;
    @Autowired
    RestTemplate restTemplate;

    MockRestServiceServer mockServer;
    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember;
    Card kakaopayCardByNormalMember;
    LocalDateTime jobStartTime;

    @BeforeEach
    void beforeEach() {
        jobLauncherTestUtils.setJob(automaticPaymentExecutionJob);
        databaseCleaner.clean();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        testInitDB.initPayment();
        setupMembersAndCards();
        jobStartTime = calculateJobStartTime();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembersAndCards() {
        freeMember = getMemberByName(testInitDB.getFreeMemberName());
        normalMember = getMemberByName(testInitDB.getNormalMemberName());
        kakaopayCardByNormalMember = getCardByKey(testInitDB.getKakaopayCardByNormalMemberKey());
    }

    private Member getMemberByName(String name) {
        return memberRepository.findByName(name).orElseThrow();
    }

    private Card getCardByKey(String key) {
        return cardRepository.findByKey(key).orElseThrow();
    }

    @Test
    public void automaticPaymentExecutionForPaidMemberTest() throws Exception {
        // given
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(normalMember.getId(), LocalDateTime.now());
        simulateBillingInfoClientSuccess(freeMember, BigDecimal.valueOf(300.00), 30L); // freeMember의 결제 가격 및 결제 실패
        simulateBillingInfoClientSuccess(normalMember, BigDecimal.valueOf(1400.00), 14000L); // normalMember의 결제 가격
        simulatePaymentSuccess(kakaopayApproveResponse);  // normalMember의 결제 성공
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
        // normalMember의 결제 성공
        assertEquals(PaymentStatus.COMPLETED_PAID, paymentsByNormalMember.getStatus());
        assertEquals(kakaopayApproveResponse.getKey(), paymentsByNormalMember.getKey());
        assertEquals(kakaopayCardByNormalMember.getType(), paymentsByNormalMember.getCardInfo().getType());
        assertEquals(kakaopayCardByNormalMember.getIssuerCorporation(), paymentsByNormalMember.getCardInfo().getIssuerCorporation());
        assertEquals(kakaopayCardByNormalMember.getBin(), paymentsByNormalMember.getCardInfo().getBin());
        // freeMember의 결제 실패
        assertEquals(PaymentStatus.FAILED, paymentsByFreeMember.getStatus());
        assertTrue(updatedFreeMember.isPaymentFailureBanned());
        assertTrue(updatedFreeApiKey.isPaymentFailureBanned());
    }

    @Test
    public void automaticPaymentExecutionForFreeMemberTest() throws Exception {
        // given
        simulateBillingInfoClientSuccess(freeMember, BigDecimal.valueOf(90.00), 9L); // freeMember의 결제 가격
        simulateBillingInfoClientSuccess(normalMember, BigDecimal.valueOf(20.00), 2L); // normalMember의 결제 가격
        JobParameters jobParameters = buildJobParameters();

        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        StepExecution stepExecution = jobExecution.getStepExecutions().iterator().next();
        Payment paymentsByNormalMember = paymentRepository.findTopByMemberIdOrderByEndedAtDesc(normalMember.getId()).orElseThrow();
        Payment paymentsByFreeMember = paymentRepository.findTopByMemberIdOrderByEndedAtDesc(freeMember.getId()).orElseThrow();

        // then
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(2, stepExecution.getReadCount());
        assertEquals(2, stepExecution.getWriteCount());
        assertEquals(1, stepExecution.getCommitCount());
        // normalMember의 결제 성공
        assertEquals(PaymentStatus.COMPLETED_FREE, paymentsByNormalMember.getStatus());
        // freeMember의 결제 성공
        assertEquals(PaymentStatus.COMPLETED_FREE, paymentsByFreeMember.getStatus());
    }

    private void simulateBillingInfoClientSuccess(Member member, BigDecimal totalCost, Long totalCount) throws Exception {
        LocalDate periodStart = getPeriodStart(member);
        LocalDate periodEnd = calculateLastSunday(jobStartTime.toLocalDate());
        BillingInfoResponse billingInfoResponse = createBillingInfoResponse(totalCount, totalCost);
        String billingInfoResponseJson = convertToJson(billingInfoResponse);
        String urlTemplate = createBillingInfoUrl(member.getId(), periodStart, periodEnd);

        mockServer.expect(requestTo(urlTemplate))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(billingInfoResponseJson, MediaType.APPLICATION_JSON));
    }

    private LocalDate getPeriodStart(Member member) {
        return paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId())
                .map(payment -> payment.getEndedAt().plusDays(1))
                .orElse(member.getCreatedAt().toLocalDate());
    }

    private BillingInfoResponse createBillingInfoResponse(Long totalCount, BigDecimal totalCost) {
        BillingInfoResponse.BillingResult billingResult = new BillingInfoResponse.BillingResult(totalCount, totalCost);
        return new BillingInfoResponse(true, billingResult);
    }

    private String createBillingInfoUrl(Long memberId, LocalDate startDate, LocalDate endDate) {
        return UriComponentsBuilder.fromHttpUrl(String.format(billingInfoProperties.getUrl() + BILLING_INFO_URL_TEMPLATE, memberId))
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString())
                .toUriString();
    }

    private void simulatePaymentSuccess(KakaopayApproveResponse kakaopayApproveResponse) throws Exception {
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
    }

    private JobParameters buildJobParameters() {
        return new JobParametersBuilder()
                .addLocalDateTime("JobStartTime", jobStartTime)
                .addLong("currentTime", System.currentTimeMillis())
                .toJobParameters();
    }

    private LocalDateTime calculateJobStartTime() {
        LocalDate lastSunday = calculateLastSunday(LocalDate.now());
        return lastSunday.plusDays(3).atTime(10, 20, 50);
    }

    private LocalDate calculateLastSunday(LocalDate date) {
        return date.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY));
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
