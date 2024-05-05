package org.changppo.account.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.changppo.account.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
class MemberControllerTest {

    @InjectMocks
    MemberController memberController;
    @Mock
    MemberService memberService;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @Test
    void readTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", id))
                .andExpect(status().isOk());

        verify(memberService).read(id);
    }

    @Test
    void requestDeleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", id))
                .andExpect(status().isOk());

        verify(memberService).requestDelete(eq(id), any(HttpServletRequest.class), any(HttpServletResponse.class));
    }

    @Test
    void cancelDeleteTest() throws Exception {
        // given
        Long id = 1L;

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/cancel/{id}", id))
                .andExpect(status().isOk());

        verify(memberService).cancelDelete(id);
    }
}
