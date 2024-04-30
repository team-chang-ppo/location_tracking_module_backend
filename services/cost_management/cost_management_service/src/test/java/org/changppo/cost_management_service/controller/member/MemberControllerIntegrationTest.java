package org.changppo.cost_management_service.controller.member;

import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.security.oauth2.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.changppo.cost_management_service.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles(value = "test")
@Transactional
class MemberControllerIntegrationTest {

    @Autowired
    WebApplicationContext context;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    TestInitDB testInitDB;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;
    MockRestServiceServer mockServer;
    Member freeMember, normalMember, bannedMember , adminMember;
    CustomOAuth2User customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BannedMember, customOAuth2AdminMember;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        setupMembers();
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

    @Test
    void readMeTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/me")
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.id").value(freeMember.getId()))
                .andExpect(jsonPath("$.result.data.name").value(freeMember.getName()))
                .andExpect(jsonPath("$.result.data.username").value(freeMember.getUsername()))
                .andExpect(jsonPath("$.result.data.profileImage").value(freeMember.getProfileImage()))
                .andExpect(jsonPath("$.result.data.roles").value(RoleType.ROLE_FREE.name()))
                .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.createdAt").exists());
    }

    @Test
    void readMeUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", freeMember.getId())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.id").value(freeMember.getId()))
                .andExpect(jsonPath("$.result.data.name").value(freeMember.getName()))
                .andExpect(jsonPath("$.result.data.username").value(freeMember.getUsername()))
                .andExpect(jsonPath("$.result.data.profileImage").value(freeMember.getProfileImage()))
                .andExpect(jsonPath("$.result.data.roles").value(RoleType.ROLE_FREE.name()))
                .andExpect(jsonPath("$.result.data.paymentFailureBannedAt").isEmpty())
                .andExpect(jsonPath("$.result.data.createdAt").exists());
    }

    @Test
    void readUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", freeMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void readAccessDeniedByNotResourceOwnerTestTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", freeMember.getId())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

//    @Test
//    void deleteMeTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when
//        mockMvc.perform(
//                        delete("/api/members/v1/me")
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
//                .andExpect(status().isOk());
//
//        // then
//        assertTrue(memberRepository.findById(freeMember.getId()).isEmpty());
//    }
//
//    @Test
//    void deleteMeUnauthorizedByNoneSessionTest() throws Exception {
//        // given, when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/me"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void deleteMeAccessDeniedByBannedMemberTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/me")
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void deleteTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", freeMember.getId())
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
//                .andExpect(status().isOk());
//
//        // then
//        assertTrue(memberRepository.findById(freeMember.getId()).isEmpty());
//
//    }
//
//    @Test
//    void deleteByAdminTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", freeMember.getId())
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
//                .andExpect(status().isOk());
//
//        // then
//        assertTrue(memberRepository.findById(freeMember.getId()).isEmpty());
//    }
//
//    @Test
//    void deleteUnauthorizedByNoneSessionTest() throws Exception {
//        // given, when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", freeMember.getId()))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", normalMember.getId())
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
//                .andExpect(status().isForbidden());
//    }
//
//    @Test
//    void deleteAccessDeniedByBannedMemberTest() throws Exception {
//        // given
//        mockServer.expect(requestTo(KAKAO_UNLINK_URL))
//                .andExpect(method(HttpMethod.POST))
//                .andRespond(withSuccess());
//
//        // when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", bannedMember.getId())
//                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BannedMember)))
//                .andExpect(status().isForbidden());
//    }
}