package org.changppo.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.DatabaseCleaner;
import org.changppo.account.TestInitDB;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.changppo.account.batch.job.JobConfig.PAYMENT_JOB;
import static org.changppo.account.builder.batch.PaymentExecutionJobRequestBuilder.buildPaymentExecutionJobRequest;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_PAYMENT_URL;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles(value = "test")
@SpringBootTest
@AutoConfigureMockMvc
public class BatchControllerTest {

    @Autowired
    JobLauncher jobLauncher;
    @Autowired
    @Qualifier(PAYMENT_JOB)
    Job paymentExecutionJob;
    @Autowired
    DatabaseCleaner databaseCleaner;
    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestInitDB testInitDB;
    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    CardRepository cardRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;

    ObjectMapper objectMapper = new ObjectMapper();
    MockRestServiceServer mockServer;
    Member banForPaymentFailureMember;
    Card kakaopayCardByBanForPaymentFailureMember;
    Payment failedPayment;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        databaseCleaner.clean();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        testInitDB.initPayment();
        setupMembers();
        setupCards();
        setupPayments();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        banForPaymentFailureMember = memberRepository.findByName(testInitDB.getBanForPaymentFailureMemberName()).orElseThrow();
    }

    private void setupCards() {
        kakaopayCardByBanForPaymentFailureMember = cardRepository.findByKey(testInitDB.getKakaopayCardByBanForPaymentFailureMemberKey()).orElseThrow();
    }

    private void setupPayments() {
        failedPayment = paymentRepository.findByKey(testInitDB.getFailedPaymentKey()).orElseThrow();
    }

    @Test
    public void batchControllerTest() throws Exception {
        // given
        simulatePaymentFailure();  // 실패 후 성공
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(banForPaymentFailureMember.getId(), LocalDateTime.now());
        simulatePaymentSuccess(kakaopayApproveResponse);
        JobParameters jobParameters = buildJobParameters(failedPayment.getMember().getId(), failedPayment.getAmount(), failedPayment.getEndedAt());
        jobLauncher.run(paymentExecutionJob, jobParameters);
        PaymentExecutionJobRequest req = buildPaymentExecutionJobRequest(failedPayment.getMember().getId(), failedPayment.getAmount(), failedPayment.getEndedAt());
        // when, then
        mockMvc.perform(
                        post("/batch/executePayment")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value(kakaopayApproveResponse.getKey()))
                .andExpect(jsonPath("$.cardType").value(kakaopayCardByBanForPaymentFailureMember.getType()))
                .andExpect(jsonPath("$.cardIssuerCorporation").value(kakaopayCardByBanForPaymentFailureMember.getIssuerCorporation()))
                .andExpect(jsonPath("$.cardBin").value(kakaopayCardByBanForPaymentFailureMember.getBin()));
    }

    private void simulatePaymentFailure() {
        mockServer.expect(requestTo(KAKAOPAY_PAYMENT_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());
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

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
