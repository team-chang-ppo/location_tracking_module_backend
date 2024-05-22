package org.changppo.account.controller.member;

import org.changppo.account.TestInitDB;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.member.MemberRepository;
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
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import static org.changppo.account.builder.member.CustomOAuth2UserBuilder.buildCustomOAuth2User;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    SessionRegistry sessionRegistry;
    @Autowired
    RestTemplate restTemplate;

    MockRestServiceServer mockServer;
    Member freeMember, normalMember, banForPaymentFailureMember, requestDeletionMember, adminBannedMemberName, adminMember;
    CustomOAuth2UserDetails customOAuth2FreeMember, customOAuth2NormalMember, customOAuth2BanForPaymentFailureMember, customOAuth2RequestDeletionMember, customOAuth2AdminMember;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        mockServer = MockRestServiceServer.createServer(restTemplate);
        testInitDB.initMember();
        setupMembers();
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
        adminBannedMemberName = memberRepository.findByName(testInitDB.getAdminBannedMemberName()).orElseThrow(MemberNotFoundException::new);
        adminMember = memberRepository.findByName(testInitDB.getAdminMemberName()).orElseThrow(MemberNotFoundException::new);
        customOAuth2FreeMember = buildCustomOAuth2User(freeMember);
        customOAuth2NormalMember = buildCustomOAuth2User(normalMember);
        customOAuth2BanForPaymentFailureMember = buildCustomOAuth2User(banForPaymentFailureMember);
        customOAuth2RequestDeletionMember = buildCustomOAuth2User(requestDeletionMember);
        customOAuth2AdminMember = buildCustomOAuth2User(adminMember);
    }

    @Test
    void readPrincipalTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                    get("/api/members/v1/principal")
                            .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.memberId").value(freeMember.getId()))
                .andExpect(jsonPath("$.result.data.roles").value(RoleType.ROLE_FREE.name()));
    }

    @Test
    void readPrincipalUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                    get("/api/members/v1/principal"))
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
                .andExpect(jsonPath("$.result.data.role").value(RoleType.ROLE_FREE.name()))
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
    void readAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", freeMember.getId())
                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void readListTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();
        long totalMembers = memberRepository.count();
        // when, then
        mockMvc.perform(
                    get("/api/members/v1/list")
                        .param("page", String.valueOf(pageable.getPageNumber()))
                        .param("size", String.valueOf(pageable.getPageSize()))
                        .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.data.content").isArray())
                .andExpect(jsonPath("$.result.data.content.length()").value((int) totalMembers))
                .andExpect(jsonPath("$.result.data.pageable.pageNumber").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.data.pageable.pageSize").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.data.totalElements").value((int) totalMembers))
                .andExpect(jsonPath("$.result.data.totalPages").value((int) Math.ceil((double) totalMembers / pageable.getPageSize())))
                .andExpect(jsonPath("$.result.data.number").value(pageable.getPageNumber()))
                .andExpect(jsonPath("$.result.data.size").value(pageable.getPageSize()))
                .andExpect(jsonPath("$.result.data.last").value(true))
                .andExpect(jsonPath("$.result.data.first").value(true))
                .andExpect(jsonPath("$.result.data.numberOfElements").value((int) totalMembers))
                .andExpect(jsonPath("$.result.data.empty").value(totalMembers == 0));
    }

    @Test
    void readListUnauthorizedByNoneSessionTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.build();
        // when, then
        mockMvc.perform(
                get("/api/members/v1/list")
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
                        get("/api/members/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize()))
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestDeleteTest() throws Exception {
        // given. when
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", freeMember.getId())
                    .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                    .andExpect(status().isOk());

        // then
        Member updatedMember = memberRepository.findById(freeMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertTrue(updatedMember.isDeletionRequested());
    }

    @Test
    void requestDeleteByAdminTest() throws Exception {
        // given. when
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", freeMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());

        // then
        Member updatedMember = memberRepository.findById(freeMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertTrue(updatedMember.isDeletionRequested());
    }

    @Test
    void requestDeleteUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", freeMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requestDeleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        //  given, when, then
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2FreeMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void requestDeleteAccessDeniedByBanForPaymentFailureMemberTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", banForPaymentFailureMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2BanForPaymentFailureMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void cancelDeleteByAdminTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/cancel/{id}", requestDeletionMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());
        // then
        Member updatedMember = memberRepository.findById(requestDeletionMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertFalse(updatedMember.isDeletionRequested());
    }

    @Test
    void cancelDeleteUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/cancel/{id}", requestDeletionMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cancelDeleteAccessDeniedByNotAdminTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/cancel/{id}", requestDeletionMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2RequestDeletionMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void banMemberByAdminTest() throws Exception {
        // given
        sessionRegistry.registerNewSession("customOAuth2NormalMemberSession", customOAuth2NormalMember);
        // when
        mockMvc.perform(
                        put("/api/members/v1/ban/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());

        // then
        Member updatedMember = memberRepository.findById(normalMember.getId()).orElseThrow(MemberNotFoundException::new);
        assertTrue(updatedMember.isAdminBanned());
        boolean isSessionExpired = sessionRegistry.getAllSessions(customOAuth2NormalMember, false).isEmpty();
        assertTrue(isSessionExpired);
    }

    @Test
    void banMemberUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/ban/{id}", normalMember.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void banMemberAccessDeniedByNotAdminTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/ban/{id}", normalMember.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }

    @Test
    void unbanMemberByAdminTest() throws Exception {
        // given, when
        mockMvc.perform(
                        put("/api/members/v1/unban/{id}", adminBannedMemberName.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2AdminMember)))
                .andExpect(status().isOk());

        // then
        Member updatedMember = memberRepository.findById(adminBannedMemberName.getId()).orElseThrow(MemberNotFoundException::new);
        assertFalse(updatedMember.isAdminBanned());
    }

    @Test
    void unbanMemberUnauthorizedByNoneSessionTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/unban/{id}", adminBannedMemberName.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unbanMemberAccessDeniedByNotAdminTest() throws Exception {
        // given, when, then
        mockMvc.perform(
                        put("/api/members/v1/unban/{id}", adminBannedMemberName.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2NormalMember)))
                .andExpect(status().isForbidden());
    }
}
