package org.changppo.account.controller.card;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.account.response.exception.common.ExceptionAdvice;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.changppo.account.response.exception.member.RoleNotFoundException;
import org.changppo.account.response.exception.member.UpdateAuthenticationFailureException;
import org.changppo.account.service.card.CardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CardControllerAdviceTest {

    @InjectMocks
    CardController cardController;
    @Mock
    CardService cardService;
    @Mock
    ResponseHandler responseHandler;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(cardController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void readCardNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new CardNotFoundException()).when(cardService).read(any());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void readRoleNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new RoleNotFoundException()).when(cardService).read(any());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void readUpdateAuthenticationFailureException() throws Exception {
        // given
        doThrow(new UpdateAuthenticationFailureException()).when(cardService).read(any());

        // when, then
        mockMvc.perform(
                        get("/api/cards/v1/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void deleteCardNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new CardNotFoundException()).when(cardService).delete(any());

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRoleNotFoundExceptionTest() throws Exception {
        // given
        doThrow(new RoleNotFoundException()).when(cardService).delete(any());

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUnsupportedPaymentGatewayExceptionTest() throws Exception {
        // given
        doThrow(new UnsupportedPaymentGatewayException()).when(cardService).delete(any());

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", 1L))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteUpdateAuthenticationFailureExceptionTest() throws Exception {
        // given
        doThrow(new UpdateAuthenticationFailureException()).when(cardService).delete(any());

        // when, then
        mockMvc.perform(
                        delete("/api/cards/v1/{id}", 1L))
                .andExpect(status().isInternalServerError());
    }
}
