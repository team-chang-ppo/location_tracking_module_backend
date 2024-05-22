package org.changppo.account.integration.card.paymentgateway.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.integration.TestInitDB;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterCancelRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayReadyResponse;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayFailException;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayRequestBuilder.*;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayReadyResponse;
import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.account.builder.response.JsonNodeBuilder.buildJsonNode;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_APPROVE_URL;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_READY_URL;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class KakaopayCardControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
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
    MockHttpSession mockSession;
    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember, bannedMember , adminMember;
    CustomOAuth2UserDetails customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BannedMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, classicApiKeyByBannedMember, bannedApiKey;
    Card card;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        mockSession = new MockHttpSession();
        testInitDB.initMember();
        testInitDB.initApiKey();
        testInitDB.initCard();
        setupMembers();
        setupApiKeys();
        setupCards();
    }

    @AfterEach
    void afterEach() {
        mockServer.reset();
    }

    private void setupMembers() {
        freeMember = memberRepository.findByName(testInitDB.getFreeMemberName()).orElseThrow(MemberNotFoundException::new);
        normalMember = memberRepository.findByName(testInitDB.getNormalMemberName()).orElseThrow(MemberNotFoundException::new);
        bannedMember = memberRepository.findByName(testInitDB.getBanForPaymentFailureMemberName()).orElseThrow(MemberNotFoundException::new);
        adminMember = memberRepository.findByName(testInitDB.getAdminMemberName()).orElseThrow(MemberNotFoundException::new);
        customOAuth2FreeMember = buildCustomOAuth2User(freeMember);
        customOAuth2NormalMember = buildCustomOAuth2User(normalMember);
        customOAuth2BannedMember = buildCustomOAuth2User(bannedMember);
        customOAuth2AdminMember = buildCustomOAuth2User(adminMember);
    }

    private void setupApiKeys() {
        freeApiKey = apiKeyRepository.findByValue(testInitDB.getFreeApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        classicApiKey = apiKeyRepository.findByValue(testInitDB.getClassicApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        bannedApiKey = apiKeyRepository.findByValue(testInitDB.getBanForPaymentFailureApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
    }

    private void setupCards() {
        card = cardRepository.findByKey(testInitDB.getKakaopayCardKey()).orElseThrow(CardNotFoundException::new);
    }
    @Test
    void registerReadyTest() throws Exception{
        // given
        KakaopayCardRegisterReadyRequest kakaopayCardRegisterReadyRequest = buildKakaopayCardRegisterReadyRequest(null);
        KakaopayReadyResponse kakaopayReadyResponse = buildKakaopayReadyResponse(LocalDateTime.now());
        String kakaopayReadyResponseJson = convertToJson(kakaopayReadyResponse);
        mockServer.expect(requestTo(KAKAOPAY_READY_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopayReadyResponseJson, MediaType.APPLICATION_JSON));

        // when, then
        mockMvc.perform(
                post("/api/cards/v1/kakaopay/register/ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kakaopayCardRegisterReadyRequest))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.nextRedirectAppUrl").value(kakaopayReadyResponse.getNext_redirect_app_url()))
                .andExpect(jsonPath("$.result.nextRedirectMobileUrl").value(kakaopayReadyResponse.getNext_redirect_mobile_url()))
                .andExpect(jsonPath("$.result.nextRedirectPcUrl").value(kakaopayReadyResponse.getNext_redirect_pc_url()))
                .andExpect(jsonPath("$.result.androidAppScheme").value(kakaopayReadyResponse.getAndroid_app_scheme()))
                .andExpect(jsonPath("$.result.iosAppScheme").value(kakaopayReadyResponse.getIos_app_scheme()));
    }

    @Test
    void registerReadyUnauthorizedByNoneSessionTest() throws Exception{
        // given
        KakaopayCardRegisterReadyRequest kakaopayCardRegisterReadyRequest = buildKakaopayCardRegisterReadyRequest(null);
        KakaopayReadyResponse kakaopayReadyResponse = buildKakaopayReadyResponse(LocalDateTime.now());
        String kakaopayReadyResponseJson = convertToJson(kakaopayReadyResponse);
        mockServer.expect(requestTo(KAKAOPAY_READY_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopayReadyResponseJson, MediaType.APPLICATION_JSON));

        // when, then
        mockMvc.perform(
                        post("/api/cards/v1/kakaopay/register/ready")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(kakaopayCardRegisterReadyRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerApproveTest() throws Exception {
        // given
        KakaopayCardRegisterApproveRequest kakaopayCardRegisterApproveRequest = buildKakaopayCardRegisterApproveRequest();
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(freeMember.getId(), LocalDateTime.now());
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_APPROVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
        mockSession.setAttribute(kakaopayCardRegisterApproveRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when
        MvcResult result = mockMvc.perform(
                                    get("/api/cards/v1/kakaopay/register/approve")
                                            .session(mockSession)
                                            .param("partner_order_id", kakaopayCardRegisterApproveRequest.getPartner_order_id())
                                            .param("pg_token", kakaopayCardRegisterApproveRequest.getPg_token())
                                            .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                                    .andExpect(status().isCreated())
                                    .andExpect(jsonPath("$.result.id").exists())
                                    .andExpect(jsonPath("$.result.type").value(kakaopayApproveResponse.getCard_info().getCard_type()))
                                    .andExpect(jsonPath("$.result.issuerCorporation").value(kakaopayApproveResponse.getCard_info().getKakaopay_issuer_corp()))
                                    .andExpect(jsonPath("$.result.bin").value(kakaopayApproveResponse.getCard_info().getBin()))
                                    .andExpect(jsonPath("$.result.paymentGateway").value(PaymentGatewayType.PG_KAKAOPAY.name()))
                                    .andExpect(jsonPath("$.result.createdAt").exists())
                                    .andReturn();

        Long id = buildJsonNode(result, objectMapper).getLongValue("result", "id");
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);

        // then
        assertEquals(id, card.getId());
        assertTrue(card.getMember().getRole().getRoleType() == RoleType.ROLE_NORMAL);
    }

    @Test
    void registerApproveUnauthorizedByNoneSessionTest() throws Exception {
        // given
        KakaopayCardRegisterApproveRequest kakaopayCardRegisterApproveRequest = buildKakaopayCardRegisterApproveRequest();
        KakaopayApproveResponse kakaopayApproveResponse = buildKakaopayApproveResponse(freeMember.getId(), LocalDateTime.now());
        String KakaopayApproveResponseJson = convertToJson(kakaopayApproveResponse);
        mockServer.expect(requestTo(KAKAOPAY_APPROVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(KakaopayApproveResponseJson, MediaType.APPLICATION_JSON));
        mockSession.setAttribute(kakaopayCardRegisterApproveRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/approve")
                    .session(mockSession)
                    .param("partner_order_id", kakaopayCardRegisterApproveRequest.getPartner_order_id())
                    .param("pg_token", kakaopayCardRegisterApproveRequest.getPg_token()))
        .       andExpect(status().isUnauthorized());
    }


    @Test
    void registerCancelTest() throws Exception {
        // given
        KakaopayCardRegisterCancelRequest kakaopayCardRegisterCancelRequest = buildKakaopayCardRegisterCancelRequest();
        mockSession.setAttribute(kakaopayCardRegisterCancelRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/cancel")
                    .session(mockSession)
                    .param("partner_order_id", kakaopayCardRegisterCancelRequest.getPartner_order_id())
                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk());
    }

    @Test
    void registerCancelUnauthorizedByNoneSessionTest() throws Exception {
        // given
        KakaopayCardRegisterCancelRequest kakaopayCardRegisterCancelRequest = buildKakaopayCardRegisterCancelRequest();
        mockSession.setAttribute(kakaopayCardRegisterCancelRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/cancel")
                    .session(mockSession)
                    .param("partner_order_id", kakaopayCardRegisterCancelRequest.getPartner_order_id()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerFailTest() throws Exception {
        // given
        KakaopayCardRegisterFailRequest kakaopayCardRegisterFailRequest = buildKakaopayCardRegisterFailRequest();
        mockSession.setAttribute(kakaopayCardRegisterFailRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/fail")
                    .session(mockSession)
                    .param("partner_order_id", kakaopayCardRegisterFailRequest.getPartner_order_id())
                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(result -> assertInstanceOf(KakaopayPaymentGatewayFailException.class, result.getResolvedException()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerFailUnauthorizedByNoneSessionTest() throws Exception {
        // given
        KakaopayCardRegisterFailRequest kakaopayCardRegisterFailRequest = buildKakaopayCardRegisterFailRequest();
        mockSession.setAttribute(kakaopayCardRegisterFailRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                    get("/api/cards/v1/kakaopay/register/fail")
                        .session(mockSession)
                        .param("partner_order_id", kakaopayCardRegisterFailRequest.getPartner_order_id()))
                    .andExpect(status().isUnauthorized());
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
