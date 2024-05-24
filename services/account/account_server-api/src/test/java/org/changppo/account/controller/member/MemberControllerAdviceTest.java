package org.changppo.account.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.changppo.account.response.exception.common.ExceptionAdvice;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.changppo.account.response.exception.member.MemberNotFoundException;
import org.changppo.account.service.application.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class MemberControllerAdviceTest {

    @InjectMocks
    MemberController memberController;
    @Mock
    MemberService memberService;
    @Mock
    ResponseHandler responseHandler;
    MockMvc mockMvc;

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(memberController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void readMemberNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(memberService).read(anyLong());

        // when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void requestDeleteMemberNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(memberService).requestDelete(anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/request/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelDeleteMemberNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(memberService).cancelDelete(anyLong());

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/cancel/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void banMemberNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(memberService).ban(anyLong());

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/ban/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void unbanMemberNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new MemberNotFoundException()).when(memberService).unban(anyLong());

        // when, then
        mockMvc.perform(
                        put("/api/members/v1/unban/{id}", 1L))
                .andExpect(status().isNotFound());
    }
}
