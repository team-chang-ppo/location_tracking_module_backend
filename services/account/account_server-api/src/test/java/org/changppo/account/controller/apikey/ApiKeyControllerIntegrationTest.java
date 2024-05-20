package org.changppo.account.controller.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.TestInitDB;
import org.changppo.account.dto.apikey.ApiKeyCreateRequest;
import org.changppo.account.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.apikey.ApiKeyRepository;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.security.oauth2.CustomOAuth2UserDetails;
import org.changppo.account.type.GradeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyCreateRequest;
import static org.changppo.account.builder.apikey.ApiKeyRequestBuilder.buildApiKeyReadAllRequest;
import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.account.builder.response.JsonNodeBuilder.buildJsonNode;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
public class ApiKeyControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestInitDB testInitDB;
    @Autowired
    ApiKeyRepository apiKeyRepository;
    @Autowired
    MemberRepository memberRepository;

    ObjectMapper objectMapper = new ObjectMapper();
    Member freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember, adminMember;
    CustomOAuth2UserDetails customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BanForPaymentFailureMember, customOAuth2RequestDeletionMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, banForPaymentFailureApiKey, banForCardDeletionApiKey, requestDeletionApiKey, adminBannedApiKey;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        testInitDB.initMember();
        testInitDB.initApiKey();
        setupMembers();
        setupApiKeys();
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
        adminBannedApiKey = apiKeyRepository.findByValue(testInitDB.getAdminBannedApiKeyValue()).orElseThrow(ApiKeyNotFoundException::new);
    }

    @Test
    void createFreeKeyTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when
        MvcResult result = mockMvc.perform(
                            post("/api/apikeys/v1/createFreeKey")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(req))
                                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.result.data.id").exists())
                            .andExpect(jsonPath("$.result.data.value").exists())
                            .andExpect(jsonPath("$.result.data.grade").value(GradeType.GRADE_FREE.name()))
                            .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.cardDeletionBannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.createdAt").exists())
                            .andReturn();

        Long id =  buildJsonNode(result, objectMapper).getLongValue("result", "data", "id");
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);

        // then
        assertEquals(id, apiKey.getId());
        assertEquals(GradeType.GRADE_FREE, apiKey.getGrade().getGradeType());
    }

    @Test
    void createFreeKeyBadRequestByNonNullMemberIdTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(freeMember.getId());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createFreeKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createFreeKeyUnauthorizedByNoneSessionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                        post("/api/apikeys/v1/createFreeKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void createFreeKeyAccessDeniedByBanForPaymentFailureMemberTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                        post("/api/apikeys/v1/createFreeKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createClassicKeyTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        MvcResult result = mockMvc.perform(
                            post("/api/apikeys/v1/createClassicKey")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(req))
                                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                            .andExpect(status().isCreated())
                            .andExpect(jsonPath("$.result.data.id").exists())
                            .andExpect(jsonPath("$.result.data.value").exists())
                            .andExpect(jsonPath("$.result.data.grade").value(GradeType.GRADE_CLASSIC.name()))
                            .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.cardDeletionBannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.createdAt").exists())
                            .andReturn();

        Long id =  buildJsonNode(result, objectMapper).getLongValue("result", "data", "id");
        ApiKey apiKey = apiKeyRepository.findById(id).orElseThrow(ApiKeyNotFoundException::new);

        // then
        assertEquals(id, apiKey.getId());
        assertEquals(GradeType.GRADE_CLASSIC, apiKey.getGrade().getGradeType());
    }

    @Test
    void createClassicKeyBadRequestByNonNullMemberIdTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(normalMember.getId());

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createClassicKeyUnauthorizedByNoneSessionTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                        post("/api/apikeys/v1/createClassicKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void createClassicKeyAccessDeniedByBanForPaymentFailureMemberTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void createClassicKeyAccessDeniedByFreeMemberTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                        post("/api/apikeys/v1/createClassicKey")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                get("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.id").value(freeApiKey.getId()))
                .andExpect(jsonPath("$.result.data.value").value(freeApiKey.getValue()))
                .andExpect(jsonPath("$.result.data.grade").value(freeApiKey.getGrade().getGradeType().name()))
                .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.cardDeletionBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.createdAt").exists());
    }

    @Test
    void readByAdminTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                get("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.id").value(freeApiKey.getId()))
                .andExpect(jsonPath("$.result.data.value").value(freeApiKey.getValue()))
                .andExpect(jsonPath("$.result.data.grade").value(freeApiKey.getGrade().getGradeType().name()))
                .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.cardDeletionBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.createdAt").exists());
    }

    @Test
    void readUnauthorizedByNoneSessionTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/{id}", freeApiKey.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given, when, then
        mockMvc.perform(
                get("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readAllTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1);
        long freeMemberApiKeyCount = apiKeyRepository.countByMemberId(freeMember.getId());

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1/member/{id}", freeMember.getId())
                        .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                        .param("size", req.getSize().toString())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.numberOfElements").value(freeMemberApiKeyCount))
                .andExpect(jsonPath("$.result.data.hasNext").value(false))
                .andExpect(jsonPath("$.result.data.apiKeyList.length()").value(freeMemberApiKeyCount));
    }

    @Test
    void readAllUnauthorizedByNoneSessionTest() throws Exception{
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/{id}", normalMember.getId())
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .param("size", req.getSize().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAllAccessDeniedByNotResourceOwnerTest() throws Exception{
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/{id}", normalMember.getId())
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readAllBadRequestByNullFirstApiKeyIdTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(null, 10);

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1/member/{id}", freeMember.getId())
                        .param("size", req.getSize().toString())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByNullSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, null);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/{id}", freeMember.getId())
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByMaxValueSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/{id}", freeMember.getId())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteTest() throws Exception {
        // given, when
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk());

        // then
        assertTrue(apiKeyRepository.findById(freeApiKey.getId()).isEmpty());
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());

        // then
        assertTrue(apiKeyRepository.findById(freeApiKey.getId()).isEmpty());
    }

    @Test
    void deleteUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        delete("/api/apikeys/v1/{id}", freeApiKey.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccessDeniedByBanForPaymentFailureApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", banForPaymentFailureApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void validateValidTrueTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/validate/{id}", freeApiKey.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.valid").value(true));
    }

    @Test
    void validateValidFalseByCardDeletionApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/validate/{id}", banForCardDeletionApiKey.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.valid").value(false));
    }

    @Test
    void validateValidFalseByPaymentFailureApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/validate/{id}", banForPaymentFailureApiKey.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.valid").value(false));
    }

    @Test
    void validateValidFalseByRequestDeletionApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/validate/{id}", requestDeletionApiKey.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.valid").value(false));
    }

    @Test
    void validateValidFalseByAdminBannedApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/validate/{id}", adminBannedApiKey.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.valid").value(false));
    }
}
