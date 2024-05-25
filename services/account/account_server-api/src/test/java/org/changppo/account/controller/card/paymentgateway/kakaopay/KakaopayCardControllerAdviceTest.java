package org.changppo.account.controller.card.paymentgateway.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;
import org.changppo.account.response.exception.card.CardCreateFailureException;
import org.changppo.account.response.exception.common.ExceptionAdvice;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayApproveFailureException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayFailException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayReadyFailureException;
import org.changppo.account.service.application.card.paymentgateway.kakaopay.KakaopayCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayRequestBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KakaopayCardControllerAdviceTest {

    @InjectMocks
    private KakaopayCardController kakaopayCardController;
    @Mock
    private KakaopayCardService kakaopayCardService;
    @Mock
    private ResponseHandler responseHandler;
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(kakaopayCardController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void registerReadyKakaopayPaymentGatewayReadyFailureExceptionTest() throws Exception {
        // given
        KakaopayCardRegisterReadyRequest kakaopayCardRegisterReadyRequest = buildKakaopayCardRegisterReadyRequest(null);
        doThrow(new KakaopayPaymentGatewayReadyFailureException()).when(kakaopayCardService).registerReady(any());
        // when, then
        mockMvc.perform(
                post("/api/cards/v1/kakaopay/register/ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kakaopayCardRegisterReadyRequest)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerApproveKakaopayPaymentGatewayApproveFailureExceptionTest() throws Exception {
        // given
        KakaopayCardRegisterApproveRequest kakaopayCardRegisterApproveRequest = buildKakaopayCardRegisterApproveRequest();
        doThrow(new KakaopayPaymentGatewayApproveFailureException()).when(kakaopayCardService).registerApprove(any());
        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/approve")
                        .param("partner_order_id", kakaopayCardRegisterApproveRequest.getPartner_order_id())
                        .param("pg_token", kakaopayCardRegisterApproveRequest.getPg_token()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerApproveCardCreateFailureExceptionTest() throws Exception {
        // given
        KakaopayCardRegisterApproveRequest kakaopayCardRegisterApproveRequest = buildKakaopayCardRegisterApproveRequest();
        doThrow(new CardCreateFailureException()).when(kakaopayCardService).registerApprove(any());
        // when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/approve")
                        .param("partner_order_id", kakaopayCardRegisterApproveRequest.getPartner_order_id())
                        .param("pg_token", kakaopayCardRegisterApproveRequest.getPg_token()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void registerFailTest() throws Exception {
        //given
        KakaopayCardRegisterFailRequest kakaopayCardRegisterFailRequest = buildKakaopayCardRegisterFailRequest();
        doThrow(new KakaopayPaymentGatewayFailException()).when(kakaopayCardService).registerFail(any());
        //when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/fail")
                        .param("partner_order_id", kakaopayCardRegisterFailRequest.getPartner_order_id()))
                .andExpect(status().isInternalServerError());
    }
}
