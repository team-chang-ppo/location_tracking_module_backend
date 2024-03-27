package org.changppo.cost_management_service.controller.member;

import org.changppo.cost_management_service.TestInitDB;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
        initDB.initDB();
    }

    @Test
    void readTest() throws Exception {
        // given
        Member member = memberRepository.findByName(initDB.getMember1()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", member.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUnauthorizedByNoneSessionTest() throws Exception {
        // given
        Member member = memberRepository.findByName(initDB.getMember1()).orElseThrow(MemberNotFoundException::new);

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", member.getId()))
                .andExpect(status().isUnauthorized());
    }
}