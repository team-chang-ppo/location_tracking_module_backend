package org.changppo.cost_management_service.controller.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.dto.apikey.ApiKeyCreateRequest;
import org.changppo.cost_management_service.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.apikey.GradeType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.response.exception.apikey.ApiKeyNotFoundException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.security.oauth2.CustomOAuth2User;
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

import static org.changppo.cost_management_service.builder.apikey.ApiKeyRequestBuilder.buildApiKeyCreateRequest;
import static org.changppo.cost_management_service.builder.apikey.ApiKeyRequestBuilder.buildApiKeyReadAllRequest;
import static org.changppo.cost_management_service.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.changppo.cost_management_service.builder.response.JsonNodeBuilder.buildJsonNode;
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
    Member freeMember, normalMember, bannedMember , adminMember;
    CustomOAuth2User customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BannedMember, customOAuth2AdminMember;
    ApiKey freeApiKey, classicApiKey, classicApiKeyByBannedMember, bannedApiKey;
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
    void createFreeKeyAccessDeniedByBannedMemberTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createFreeKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
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
    void createClassicKeyAccessDeniedByBannedMemberTest() throws Exception {
        // given
        ApiKeyCreateRequest req = buildApiKeyCreateRequest(null);

        // when, then
        mockMvc.perform(
                post("/api/apikeys/v1/createClassicKey")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
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
    void readAllMeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1);
        long freeMemberApiKeyCount = apiKeyRepository.countByMemberId(freeMember.getId());

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/me")
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.numberOfElements").value(freeMemberApiKeyCount))
                .andExpect(jsonPath("$.result.data.hasNext").value(false))
                .andExpect(jsonPath("$.result.data.apiKeyList.length()").value(freeMemberApiKeyCount));
    }

    @Test
    void readAllMeUnauthorizedByNoneSessionTest() throws Exception{
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/me")
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .param("size", req.getSize().toString()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAllMeBadRequestByNullFirstApiKeyIdTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(null, 10);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/me")
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllMeBadRequestByNullSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, null);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/me")
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllMeBadRequestByMaxValueSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1/member/me")
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
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

//    @Test
//    void deleteAccessDeniedByBannedMemberTest() throws Exception {
//        // given, when, then
//        mockMvc.perform(
//                delete("/api/apikeys/v1/{id}", classicApiKeyByBannedMember.getId())
//                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void deleteAccessDeniedByBannedApiKeyTest() throws Exception {
//        // given, when, then
//        mockMvc.perform(
//                delete("/api/apikeys/v1/{id}", bannedApiKey.getId())
//                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
//                .andExpect(status().isForbidden());
//    }
}
