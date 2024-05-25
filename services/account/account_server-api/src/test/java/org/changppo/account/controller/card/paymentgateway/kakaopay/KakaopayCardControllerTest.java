package org.changppo.account.controller.card.paymentgateway.kakaopay;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterCancelRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class KakaopayCardControllerTest {

    @InjectMocks
    private KakaopayCardController kakaopayCardController;

    @Mock
    private KakaopayCardService kakaopayCardService;

    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(kakaopayCardController).build();
    }

    @Test
    void registerReadyTest() throws Exception {
        //given
        KakaopayCardRegisterReadyRequest request = buildKakaopayCardRegisterReadyRequest(null);
        //when, then
        mockMvc.perform(
                post("/api/cards/v1/kakaopay/register/ready")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void registerApproveTest() throws Exception {
        //given
        KakaopayCardRegisterApproveRequest request = buildKakaopayCardRegisterApproveRequest();
        //when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/approve")
                        .param("partner_order_id", request.getPartner_order_id())
                        .param("pg_token", request.getPg_token()))
                .andExpect(status().isCreated());
    }

    @Test
    void registerCancelTest() throws Exception {
        //given
        KakaopayCardRegisterCancelRequest request = buildKakaopayCardRegisterCancelRequest();
        //when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/cancel")
                        .param("partner_order_id", request.getPartner_order_id()))
                .andExpect(status().isOk());
    }

    @Test
    void registerFailTest() throws Exception {
        //given
        KakaopayCardRegisterFailRequest request = buildKakaopayCardRegisterFailRequest();
        //when, then
        mockMvc.perform(
                get("/api/cards/v1/kakaopay/register/fail")
                        .param("partner_order_id", request.getPartner_order_id()))
                .andExpect(status().isOk());
    }
}
