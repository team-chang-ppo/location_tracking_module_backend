package org.changppo.cost_management_service.controller.apikey;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.dto.apikey.ApiKeyCreateRequest;
import org.changppo.cost_management_service.dto.apikey.ApiKeyReadAllRequest;
import org.changppo.cost_management_service.entity.apikey.ApiKey;
import org.changppo.cost_management_service.entity.apikey.GradeType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.exception.ApiKeyNotFoundException;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.security.oauth.CustomOAuth2User;
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
import static org.changppo.cost_management_service.builder.response.JsonResponseBuilder.buildJsonResponse;
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
                            .andExpect(jsonPath("$.result.data.bannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.createdAt").exists())
                            .andReturn();

        Long id =  buildJsonResponse(result).getLongValue("result", "data", "id");
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
                            .andExpect(jsonPath("$.result.data.bannedAt").isEmpty())
                            .andExpect(jsonPath("$.result.data.createdAt").exists())
                            .andReturn();

        Long id =  buildJsonResponse(result).getLongValue("result", "data", "id");
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
                .andExpect(jsonPath("$.result.data.bannedAt").isEmpty())
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
                .andExpect(jsonPath("$.result.data.bannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.createdAt").exists());
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
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE - 1, null);
        long freeMemberApiKeyCount = apiKeyRepository.countByMemberId(freeMember.getId());

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1")
                        .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                        .param("size", req.getSize().toString())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.numberOfElements").value(freeMemberApiKeyCount))
                .andExpect(jsonPath("$.result.data.hasNext").value(false));
    }

    @Test
    void readAllBadRequestByNullFirstApiKeyIdTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(null, 10, null);

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1")
                        .param("size", req.getSize().toString())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByNullSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, null, null);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1")
                                .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByMaxValueSizeTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, Integer.MAX_VALUE, null);

        // when, then
        mockMvc.perform(
                        get("/api/apikeys/v1")
                                .param("size", req.getSize().toString())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void readAllBadRequestByNonNullMemberIdTest() throws Exception {
        // given
        ApiKeyReadAllRequest req = buildApiKeyReadAllRequest(1L, 10, freeMember.getId());

        // when, then
        mockMvc.perform(
                get("/api/apikeys/v1")
                        .param("firstApiKeyId", req.getFirstApiKeyId().toString())
                        .param("size", req.getSize().toString())
                        .param("memberId", req.getMemberId().toString())
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
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", freeApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccessDeniedByBannedMemberTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", classicApiKeyByBannedMember.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteAccessDeniedByBannedApiKeyTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                delete("/api/apikeys/v1/{id}", bannedApiKey.getId())
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }
}
