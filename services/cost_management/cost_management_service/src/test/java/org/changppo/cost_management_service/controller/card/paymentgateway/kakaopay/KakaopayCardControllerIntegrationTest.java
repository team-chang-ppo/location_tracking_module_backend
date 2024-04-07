package org.changppo.cost_management_service.controller.card.paymentgateway.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterCancelRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.KakaopayApproveResponse;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.KakaopayReadyResponse;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.cost_management_service.response.exception.card.CardNotFoundException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.response.exception.paymentgateway.KakaopayPaymentGatewayFailException;
import org.changppo.cost_management_service.security.oauth2.CustomOAuth2User;
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

import static org.changppo.cost_management_service.builder.card.KakaopayRequestBuilder.*;
import static org.changppo.cost_management_service.builder.card.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.cost_management_service.builder.card.KakaopayResponseBuilder.buildKakaopayReadyResponse;
import static org.changppo.cost_management_service.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.cost_management_service.builder.response.JsonNodeBuilder.buildJsonNode;
import static org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_APPROVE_URL;
import static org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_READY_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
    CustomOAuth2User customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BannedMember, customOAuth2AdminMember;
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

    private void setupMembers() {
        freeMember = memberRepository.findByName(testInitDB.getFreeMemberName()).orElseThrow(MemberNotFoundException::new);
        normalMember = memberRepository.findByName(testInitDB.getNormalMemberName()).orElseThrow(MemberNotFoundException::new);
        bannedMember = memberRepository.findByName(testInitDB.getBannedMemberName()).orElseThrow(MemberNotFoundException::new);
        adminMember = memberRepository.findByName(testInitDB.getAdminMemberName()).orElseThrow(MemberNotFoundException::new);
        customOAuth2FreeMember = buildCustomOAuth2User(freeMember);
        customOAuth2NormalMember = buildCustomOAuth2User(normalMember);
        customOAuth2BannedMember = buildCustomOAuth2User(bannedMember);
        customOAuth2AdminMember = buildCustomOAuth2User(adminMember);
    }

    private void setupApiKeys() {
        freeApiKey = apiKeyRepository.findByValue(testInitDB.getFreeApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        classicApiKey = apiKeyRepository.findByValue(testInitDB.getClassicApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
        classicApiKeyByBannedMember = apiKeyRepository.findByValue(testInitDB.getClassicApiKeyByBannedMemberValue()).orElseThrow(ApiKeyNotFoundException::new);
        bannedApiKey = apiKeyRepository.findByValue(testInitDB.getBannedApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
    }

    private void setupCards() {
        card = cardRepository.findByKey(testInitDB.getTestCardKey()).orElseThrow(CardNotFoundException::new);
    }
    @Test
    void readyTest() throws Exception{
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
                .andExpect(jsonPath("$.result.data.nextRedirectAppUrl").value(kakaopayReadyResponse.getNext_redirect_app_url()))
                .andExpect(jsonPath("$.result.data.nextRedirectMobileUrl").value(kakaopayReadyResponse.getNext_redirect_mobile_url()))
                .andExpect(jsonPath("$.result.data.nextRedirectPcUrl").value(kakaopayReadyResponse.getNext_redirect_pc_url()))
                .andExpect(jsonPath("$.result.data.androidAppScheme").value(kakaopayReadyResponse.getAndroid_app_scheme()))
                .andExpect(jsonPath("$.result.data.iosAppScheme").value(kakaopayReadyResponse.getIos_app_scheme()));
    }

    @Test
    void approveTest() throws Exception {
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
                                    .andExpect(jsonPath("$.result.data.id").exists())
                                    .andExpect(jsonPath("$.result.data.type").value(kakaopayApproveResponse.getCard_info().getCard_type()))
                                    .andExpect(jsonPath("$.result.data.issuerCorporation").value(kakaopayApproveResponse.getCard_info().getKakaopay_issuer_corp()))
                                    .andExpect(jsonPath("$.result.data.bin").value(kakaopayApproveResponse.getCard_info().getBin()))
                                    .andExpect(jsonPath("$.result.data.paymentGateway").value(PaymentGatewayType.PG_KAKAOPAY.name()))
                                    .andExpect(jsonPath("$.result.data.createdAt").exists())
                                    .andReturn();

        Long id = buildJsonNode(result, objectMapper).getLongValue("result", "data", "id");
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);

        // then
        assertEquals(id, card.getId());
        assertTrue(card.getMember().getMemberRoles().stream().allMatch(role -> role.getRole().getRoleType() == RoleType.ROLE_NORMAL));
    }

    @Test
    void cancelTest() throws Exception {
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
    void failTest() throws Exception {
        // given
        KakaopayCardRegisterFailRequest kakaopayCardRegisterFailRequest = buildKakaopayCardRegisterFailRequest();
        mockSession.setAttribute(kakaopayCardRegisterFailRequest.getPartner_order_id(), "tid_" + UUID.randomUUID());

        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/fail")
                    .session(mockSession)
                    .param("partner_order_id", kakaopayCardRegisterFailRequest.getPartner_order_id())
                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof KakaopayPaymentGatewayFailException))
                .andExpect(status().isInternalServerError());
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
