package org.changppo.account.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.dto.payment.PaymentReadAllRequest;
import org.changppo.account.service.application.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.changppo.account.builder.payment.PaymentRequestBuilder.buildPaymentReadAllRequest;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;
    @Mock
    private PaymentService paymentService;
    private MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())  // Pageable 처리를 위한 설정
                .build();
    }

    @Test
    void repaymentTest() throws Exception {
        // given
        Long id = 1L;
        // when, then
        mockMvc.perform(
                post("/api/payments/v1/repayment/{id}", id))
                .andExpect(status().isOk());
        verify(paymentService).repayment(id);
    }

    @Test
    void readAllTest() throws Exception {
        // given
        Long id = 1L;
        PaymentReadAllRequest req = buildPaymentReadAllRequest(1L, 10);

        // when, then
        mockMvc.perform(
                get("/api/payments/v1/member/{id}", id)
                        .param("firstApiKeyId", req.getLastPaymentId().toString())
                        .param("size", req.getSize().toString()))
                .andExpect(status().isOk());

        verify(paymentService).readAll(eq(id), any(PaymentReadAllRequest.class));
    }

    @Test
    void readListTest() throws Exception {
        // given
        Pageable pageable = PageableBuilder.buildPage();

        // when, then
        mockMvc.perform(
                        get("/api/payments/v1/list")
                                .param("page", String.valueOf(pageable.getPageNumber()))
                                .param("size", String.valueOf(pageable.getPageSize())))
                .andExpect(status().isOk());

        verify(paymentService).readList(pageable);
    }
}
