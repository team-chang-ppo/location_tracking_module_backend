package org.changppo.account.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.TestInitDB;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.payment.PaymentExecutionJobClient;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.paymentgateway.dto.PaymentResponse;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.payment.PaymentRepository;
import org.changppo.account.response.ClientResponse;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.security.oauth2.CustomOAuth2User;
import org.changppo.account.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;

import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.account.builder.payment.PaymentRequestBuilder.buildPaymentReadAllRequest;
import static org.changppo.account.builder.payment.PaymentResponseBuilder.buildPaymentResponse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class PaymentControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @MockBean
    PaymentExecutionJobClient paymentExecutionJobClient;
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
    Member freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember, adminMember;
    CustomOAuth2User customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BanForPaymentFailureMember, customOAuth2RequestDeletionMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, banForPaymentFailureApiKey, banForCardDeletionApiKey, requestDeletionApiKey;
    Card kakaopayCard;
    Payment successfulPayment, failedPayment;

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
        successfulPayment = paymentRepository.findByKey(testInitDB.getSuccessfulPaymentKey()).orElseThrow(PaymentNotFoundException::new);
        failedPayment = paymentRepository.findByKey(testInitDB.getFailedPaymentKey()).orElseThrow(PaymentNotFoundException::new);
    }

    @Test
    void repaymentTest() throws Exception{
        // given
        PaymentResponse paymentResponse = buildPaymentResponse();
        when(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).thenReturn(ClientResponse.success(paymentResponse));
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isOk());
        //then
        Payment updatedPayment = paymentRepository.findById(failedPayment.getId()).orElseThrow(PaymentNotFoundException::new);
        assertEquals(updatedPayment.getStatus(), PaymentStatus.COMPLETED_PAID);
        assertEquals(updatedPayment.getKey(), paymentResponse.getKey());
        assertEquals(updatedPayment.getCardInfo().getType(), paymentResponse.getCardType());
        assertEquals(updatedPayment.getCardInfo().getIssuerCorporation(), paymentResponse.getCardIssuerCorporation());
        assertEquals(updatedPayment.getCardInfo().getBin(), paymentResponse.getCardBin());
    }

    @Test
    void repaymentByAdminTest() throws Exception{
        // given
        PaymentResponse paymentResponse = buildPaymentResponse();
        when(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).thenReturn(ClientResponse.success(paymentResponse));
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());
        //then
        Payment updatedPayment = paymentRepository.findById(failedPayment.getId()).orElseThrow(PaymentNotFoundException::new);
        assertEquals(updatedPayment.getStatus(), PaymentStatus.COMPLETED_PAID);
        assertEquals(updatedPayment.getKey(), paymentResponse.getKey());
        assertEquals(updatedPayment.getCardInfo().getType(), paymentResponse.getCardType());
        assertEquals(updatedPayment.getCardInfo().getIssuerCorporation(), paymentResponse.getCardIssuerCorporation());
        assertEquals(updatedPayment.getCardInfo().getBin(), paymentResponse.getCardBin());
    }

    @Test
    void repaymentUnauthorizedByNoneSessionTest() throws Exception {
        // given
        PaymentResponse paymentResponse = buildPaymentResponse();
        when(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).thenReturn(ClientResponse.success(paymentResponse));
        // when, then
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void repaymentAccessDeniedByNotPaymentFailureTest() throws Exception {
        // given
        PaymentResponse paymentResponse = buildPaymentResponse();
        when(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).thenReturn(ClientResponse.success(paymentResponse));
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", successfulPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void repaymentAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given
        PaymentResponse paymentResponse = buildPaymentResponse();
        when(paymentExecutionJobClient.PaymentExecutionJob(any(PaymentExecutionJobRequest.class))).thenReturn(ClientResponse.success(paymentResponse));
        // when
        mockMvc.perform(post("/api/payments/v1/repayment/{id}", failedPayment.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
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
                .andExpect(jsonPath("$.result.data.numberOfElements").value(normalMemberPaymentCount))
                .andExpect(jsonPath("$.result.data.hasNext").value(false))
                .andExpect(jsonPath("$.result.data.paymentList.length()").value(normalMemberPaymentCount));
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
                .andExpect(jsonPath("$.result.data.numberOfElements").value(normalMemberPaymentCount))
                .andExpect(jsonPath("$.result.data.hasNext").value(false))
                .andExpect(jsonPath("$.result.data.paymentList.length()").value(normalMemberPaymentCount));
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

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
