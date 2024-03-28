package org.changppo.cost_management_service.controller.member;

import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.security.oauth.CustomOAuth2User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;
import java.io.IOException;
import java.util.stream.Collectors;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    TestInitDB initDB;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RestTemplate restTemplate;
    MockRestServiceServer mockServer;

    @BeforeEach
    void beforeEach() throws IOException {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void readTest() throws Exception {
        // given
        Member member = memberRepository.findByName(initDB.getMember1Name()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", member.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteTest() throws Exception {
        // given
        Member member = memberRepository.findByName(initDB.getMember1Name()).orElseThrow(MemberNotFoundException::new);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(member.getId(), member.getName(), member.getRoles().stream()
                                                                                                    .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                                                                    .collect(Collectors.toSet()));
        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", member.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2User)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteByAdminTest() throws Exception {
        // given
        Member admin = memberRepository.findByName(initDB.getAdminName()).orElseThrow(MemberNotFoundException::new);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(admin.getId(), admin.getName(), admin.getRoles().stream()
                                                                                                    .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                                                                    .collect(Collectors.toSet()));
        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", admin.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2User)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUnauthorizedByNoneSessionTest() throws Exception {
        // given
        Member member = memberRepository.findByName(initDB.getMember1Name()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", member.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteAccessDeniedByNotResourceOwnerTest() throws Exception {
        // given
        Member member1 = memberRepository.findByName(initDB.getMember1Name()).orElseThrow(MemberNotFoundException::new);
        Member member2 = memberRepository.findByName(initDB.getMember2Name()).orElseThrow(MemberNotFoundException::new);
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(member1.getId(), member1.getName(), member1.getRoles().stream()
                                                                                                        .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                                                                                        .collect(Collectors.toSet()));
        mockServer.expect(requestTo("https://kapi.kakao.com/v1/user/unlink"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", member2.getId())
                                .with(SecurityMockMvcRequestPostProcessors.oauth2Login().oauth2User(customOAuth2User)))
                .andExpect(status().isForbidden());
    }
}