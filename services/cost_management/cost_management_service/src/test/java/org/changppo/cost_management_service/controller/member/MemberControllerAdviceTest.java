package org.changppo.cost_management_service.controller.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.changppo.cost_management_service.exception.MemberNotFoundException;
import org.changppo.cost_management_service.exception.MemberUnlinkFailureException;
import org.changppo.cost_management_service.exception.advice.ExceptionAdvice;
import org.changppo.cost_management_service.exception.response.ResponseHandler;
import org.changppo.cost_management_service.service.member.MemberService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
        doThrow(MemberNotFoundException.class).when(memberService).read(anyLong());

        // when, then
        mockMvc.perform(
                        get("/api/members/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMemberNotFoundExceptionTest() throws Exception{
        // given
        doThrow(MemberNotFoundException.class).when(memberService).delete(anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when, then
        mockMvc.perform(
                        delete("/api/members/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteMemberUnlinkFailureExceptionTest() throws Exception {
        // given
        doThrow(MemberUnlinkFailureException.class).when(memberService).delete(anyLong(), any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when, then
        mockMvc.perform(delete("/api/members/v1/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }

}