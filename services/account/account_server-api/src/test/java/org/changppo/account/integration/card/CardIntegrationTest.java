package org.changppo.account.integration.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.integration.TestInitDB;
import org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.security.sign.CustomOAuth2UserDetails;
import org.changppo.account.type.RoleType;
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
import java.time.LocalDateTime;

import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.KAKAOPAY_SUBSCRIPTION_INACTIVE_URL;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class CardIntegrationTest {

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
    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember, adminMember;
    CustomOAuth2UserDetails customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BanForPaymentFailureMember, customOAuth2RequestDeletionMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, banForPaymentFailureApiKey, banForCardDeletionApiKey, requestDeletionApiKey;
    Card kakaopayCard;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
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

    @Test
    void readTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(kakaopayCard.getId()))
                .andExpect(jsonPath("$.result.type").value(kakaopayCard.getType()))
                .andExpect(jsonPath("$.result.issuerCorporation").value(kakaopayCard.getIssuerCorporation()))
                .andExpect(jsonPath("$.result.bin").value(kakaopayCard.getBin()))
                .andExpect(jsonPath("$.result.paymentGateway").value(kakaopayCard.getPaymentGateway().getPaymentGatewayType().name()))
                .andExpect(jsonPath("$.result.createdAt").exists());
    }

    @Test
    void readByAdminTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").value(kakaopayCard.getId()))
                .andExpect(jsonPath("$.result.type").value(kakaopayCard.getType()))
                .andExpect(jsonPath("$.result.issuerCorporation").value(kakaopayCard.getIssuerCorporation()))
                .andExpect(jsonPath("$.result.bin").value(kakaopayCard.getBin()))
                .andExpect(jsonPath("$.result.paymentGateway").value(kakaopayCard.getPaymentGateway().getPaymentGatewayType().name()))
                .andExpect(jsonPath("$.result.createdAt").exists());
    }

    @Test
    void readUnauthorizedByNoneSessionTestTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", kakaopayCard.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readAllMeTest() throws Exception {
        // given
        long normalMemberCardCount = cardRepository.countByMemberId(normalMember.getId());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/me")
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cardList.length()").value(normalMemberCardCount));
    }

    @Test
    void readAllMeUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                    get("/api/cards/v1/member/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAllTest() throws Exception {
        // given
        long normalMemberCardCount = cardRepository.countByMemberId(normalMember.getId());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cardList.length()").value(normalMemberCardCount));
    }

    @Test
    void readAllByAdminTest() throws Exception {
        // given
        long normalMemberCardCount = cardRepository.countByMemberId(normalMember.getId());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.cardList.length()").value(normalMemberCardCount));
    }

    @Test
    void readAllUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/{id}", normalMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAllAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/cards/v1/member/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readListTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();
        long totalCards = cardRepository.count();
        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content.length()").value((int) totalCards))
                .andExpect(jsonPath("$.result.pageable.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.pageable.pageSize").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.totalElements").value((int) totalCards))
                .andExpect(jsonPath("$.result.totalPages").value((int) Math.ceil((double) totalCards / pageable.getPageSize())))
                .andExpect(jsonPath("$.result.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.last").value(true))
                .andExpect(jsonPath("$.result.first").value(true))
                .andExpect(jsonPath("$.result.numberOfElements").value((int) totalCards))
                .andExpect(jsonPath("$.result.empty").value(totalCards == 0));
    }

    @Test
    void readListUnauthorizedByNoneSessionTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();
        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readListAccessDeniedByNotAdminTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();
        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteTest() throws Exception {
        // given
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));

        // when
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isOk());

        // then
        assertTrue(cardRepository.findById(kakaopayCard.getId()).isEmpty());
        Member member = memberRepository.findById(normalMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertTrue(member.getRole().getRoleType() == RoleType.ROLE_FREE);
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));

        // when
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());

        // then
        assertTrue(cardRepository.findById(kakaopayCard.getId()).isEmpty());
        Member member = memberRepository.findById(normalMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertTrue(member.getRole().getRoleType() == RoleType.ROLE_FREE);
    }

    @Test
    void deleteUnauthorizedByNoneSessionTest() throws Exception {
        // given
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", kakaopayCard.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccessDeniedByFreeMemberTest() throws Exception {
        // given
        String kakaopaySubscriptionInactiveResponseJson = convertToJson(KakaopayResponseBuilder.buildKakaopaySubscriptionInactiveResponse(LocalDateTime.now()));
        mockServer.expect(requestTo(KAKAOPAY_SUBSCRIPTION_INACTIVE_URL))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(kakaopaySubscriptionInactiveResponseJson, MediaType.APPLICATION_JSON));

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", kakaopayCard.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    private String convertToJson(Object object) throws IOException {
        return objectMapper.writeValueAsString(object);
    }
}
