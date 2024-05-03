package org.changppo.account.controller.member;

import org.changppo.account.security.PrincipalHandler;
import org.changppo.account.service.member.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    void readMeTest() throws Exception {
        // given
        Long id = 1L;
        try (MockedStatic<PrincipalHandler> mocked = mockStatic(PrincipalHandler.class)) {
            mocked.when(PrincipalHandler::extractId).thenReturn(id);

            // when, then
            mockMvc.perform(
                            get("/api/members/v1/me"))
                    .andExpect(status().isOk());

            verify(memberService).read(id);
        }
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

//    @Test
//    void deleteTest() throws Exception {
//        // given
//        Long id = 1L;
//
//        // when, then
//        mockMvc.perform(
//                        delete("/api/members/v1/{id}", id))
//                .andExpect(status().isOk());
//
//        verify(memberService).delete(eq(id), any(HttpServletRequest.class), any(HttpServletResponse.class));
//
//    }

}