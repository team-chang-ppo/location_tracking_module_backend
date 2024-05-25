package org.changppo.account.integration.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.batch.dto.PaymentExecutionJobResponse;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.config.BatchServerUrlProperties;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.integration.TestInitDB;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.PaymentStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.changppo.account.batch.PaymentExecutionJobClient.PAYMENT_EXECUTION_JOB_URL;
import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.account.builder.payment.PaymentExecutionJobResponseBuilder.buildPaymentExecutionJobResponse;
import static org.changppo.account.builder.payment.PaymentRequestBuilder.buildPaymentReadAllRequest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PaymentIntegrationTest {

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
    ApiKeyRepository apiKeyRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    BatchServerUrlProperties batchServerUrlProperties;

    MockRestServiceServer mockServer;
    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember, adminMember;
    CustomOAuth2UserDetails customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BanForPaymentFailureMember, customOAuth2RequestDeletionMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, banForPaymentFailureApiKey, banForCardDeletionApiKey, requestDeletionApiKey;
    Card kakaopayCard;
    Payment successfulPaidPayment, successfulFreePayment, failedPayment;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        testInitDB.initPayment();
        setupMembers();
        setupApiKeys();
        setupCards();
        setupPayments();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        freeMember = memberRepository.findByName(testInitDB.getFreeMemberName()).orElseThrow(MemberNotFoundException::new);
        normalMember = memberRepository.findByName(testInitDB.getNormalMemberName()).orElseThrow(MemberNotFoundException::new);
        banForPaymentFailureMember = memberRepository.findByName(testInitDB.getBanForPaymentFailureMemberName()).orElseThrow(MemberNotFoundException::new);
        requestDeletionMember = memberRepository.findByName(testInitDB.getRequestDeletionMemberName()).orElseThrow(MemberNotFoundException::new);
        adminMember = memberRepository.findByName(testInitDB.getAdminMemberName()).orElseThrow(MemberNotFoundException::new);
        customOAuth2FreeMember = buildCustomOAuth2User(freeMember);
        customOAuth2NormalMember = buildCustomOAuth2User(normalMember);
        customOAuth2BanForPaymentFailureMember = buildCustomOAuth2User(banForPaymentFailureMember);
        customOAuth2RequestDeletionMember = buildCustomOAuth2User(requestDeletionMember);
        customOAuth2AdminMember = buildCustomOAuth2User(adminMember);
    }

    private void setupApiKeys() {
        freeApiKey = apiKeyRepository.findByValue(testInitDB.getFreeApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        classicApiKey = apiKeyRepository.findByValue(testInitDB.getClassicApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        banForPaymentFailureApiKey = apiKeyRepository.findByValue(testInitDB.getBanForPaymentFailureApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        banForCardDeletionApiKey = apiKeyRepository.findByValue(testInitDB.getBanForCardDeletionApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        requestDeletionApiKey = apiKeyRepository.findByValue(testInitDB.getRequestDeletionApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
    }

    private void setupCards() {
        kakaopayCard = cardRepository.findByKey(testInitDB.getKakaopayCardKey()).orElseThrow(CardNotFoundException::new);
    }

    private void setupPayments() {
        successfulPaidPayment = paymentRepository.findByKey(testInitDB.getSuccessfulPaidPaymentKey()).orElseThrow(PaymentNotFoundException::new);
        successfulFreePayment = paymentRepository.findByKey(testInitDB.getSuccessfulFreePaymentKey()).orElseThrow(PaymentNotFoundException::new);
        failedPayment = paymentRepository.findByKey(testInitDB.getFailedPaymentKey()).orElseThrow(PaymentNotFoundException::new);
    }

    @Test
    void repaymentTest() throws Exception{
        // given
        PaymentExecutionJobResponse paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
        simulatePaymentExecutionJobClientSuccess(paymentExecutionJobResponse);
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isOk());
        //then
        Payment updatedPayment = paymentRepository.findById(failedPayment.getId()).orElseThrow(PaymentNotFoundException::new);
        assertEquals(updatedPayment.getStatus(), PaymentStatus.COMPLETED_PAID);
        assertEquals(updatedPayment.getKey(), paymentExecutionJobResponse.getKey());
        assertEquals(updatedPayment.getCardInfo().getType(), paymentExecutionJobResponse.getCardType());
        assertEquals(updatedPayment.getCardInfo().getIssuerCorporation(), paymentExecutionJobResponse.getCardIssuerCorporation());
        assertEquals(updatedPayment.getCardInfo().getBin(), paymentExecutionJobResponse.getCardBin());
    }

    @Test
    void repaymentByAdminTest() throws Exception{
        // given
        PaymentExecutionJobResponse paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
        simulatePaymentExecutionJobClientSuccess(paymentExecutionJobResponse);
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());
        //then
        Payment updatedPayment = paymentRepository.findById(failedPayment.getId()).orElseThrow(PaymentNotFoundException::new);
        assertEquals(updatedPayment.getStatus(), PaymentStatus.COMPLETED_PAID);
        assertEquals(updatedPayment.getKey(), paymentExecutionJobResponse.getKey());
        assertEquals(updatedPayment.getCardInfo().getType(), paymentExecutionJobResponse.getCardType());
        assertEquals(updatedPayment.getCardInfo().getIssuerCorporation(), paymentExecutionJobResponse.getCardIssuerCorporation());
        assertEquals(updatedPayment.getCardInfo().getBin(), paymentExecutionJobResponse.getCardBin());
    }

    @Test
    void repaymentUnauthorizedByNoneSessionTest() throws Exception {
        // given
        PaymentExecutionJobResponse paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
        simulatePaymentExecutionJobClientSuccess(paymentExecutionJobResponse);
        // when, then
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void repaymentAccessDeniedByNotPaymentFailureTest() throws Exception {
        // given
        PaymentExecutionJobResponse paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
        simulatePaymentExecutionJobClientSuccess(paymentExecutionJobResponse);
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", successfulPaidPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void repaymentAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given
        PaymentExecutionJobResponse paymentExecutionJobResponse = buildPaymentExecutionJobResponse();
        simulatePaymentExecutionJobClientSuccess(paymentExecutionJobResponse);
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    private void simulatePaymentExecutionJobClientSuccess(PaymentExecutionJobResponse paymentExecutionJobResponse) throws IOException {
        mockServer.expect(requestTo(batchServerUrlProperties.getUrl() + PAYMENT_EXECUTION_JOB_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(paymentExecutionJobResponse), MediaType.APPLICATION_JSON));
    }

    @Test
    void readAllTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(Long.MAX_VALUE - 1,Integer.MAX_VALUE - 1);
        long normalMemberPaymentCount = paymentRepository.countByMemberId(normalMember.getId());
        // when, then
        mockMvc.perform(
                get("/api/payments/v1/member/{id}", normalMember.getId())
                        .param("lastPaymentId", req.getLastPaymentId().toString())
                        .param("size", req.getSize().toString())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.result.numberOfElements").value(normalMemberPaymentCount-1))
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.paymentList.length()").value(normalMemberPaymentCount-1));
    }

    @Test
    void readAllByAdminTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(Long.MAX_VALUE - 1,Integer.MAX_VALUE - 1);
        long normalMemberPaymentCount = paymentRepository.countByMemberId(normalMember.getId());
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/member/{id}", normalMember.getId())
                                .param("lastPaymentId", req.getLastPaymentId().toString())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.numberOfElements").value(normalMemberPaymentCount-1))
                .andExpect(jsonPath("$.result.hasNext").value(false))
                .andExpect(jsonPath("$.result.paymentList.length()").value(normalMemberPaymentCount-1));
    }

    @Test
    void readAllUnauthorizedByNoneSessionTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(Long.MAX_VALUE - 1,Integer.MAX_VALUE - 1);
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/member/{id}", normalMember.getId())
                                .param("lastPaymentId", req.getLastPaymentId().toString())
                                .param("size", req.getSize().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAllAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(Long.MAX_VALUE - 1,Integer.MAX_VALUE - 1);
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/member/{id}", normalMember.getId())
                                .param("lastPaymentId", req.getLastPaymentId().toString())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readAllBadRequestByNullSizeTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(1L, null);
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/member/{id}", normalMember.getId())
                                .param("lastPaymentId", req.getLastPaymentId().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByMaxValueSizeTest() throws Exception {
        // given
        PaymentReadAllRequest req = buildPaymentReadAllRequest(1L, Integer.MAX_VALUE);
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/member/{id}", normalMember.getId())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readListTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.buildPage();
        long totalPayments = paymentRepository.count();
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content.length()").value((int) totalPayments))
                .andExpect(jsonPath("$.result.pageable.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.pageable.pageSize").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.totalElements").value((int) totalPayments))
                .andExpect(jsonPath("$.result.totalPages").value((int) Math.ceil((double) totalPayments / pageable.getPageSize())))
                .andExpect(jsonPath("$.result.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.last").value(true))
                .andExpect(jsonPath("$.result.first").value(true))
                .andExpect(jsonPath("$.result.numberOfElements").value((int) totalPayments))
                .andExpect(jsonPath("$.result.empty").value(totalPayments == 0));
    }

    @Test
    void readListUnauthorizedByNoneSessionTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.buildPage();
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readListAccessDeniedByNotAdminTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.buildPage();
        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
