package org.changppo.account.controller.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.changppo.account.response.exception.common.ExceptionAdvice;
import org.changppo.account.response.exception.common.ResponseHandler;
import org.changppo.account.response.exception.payment.PaymentExecutionFailureException;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PaymentControllerAdviceTest {

    @InjectMocks
    private PaymentController paymentController;
    @Mock
    private PaymentService paymentService;
    @Mock
    ResponseHandler responseHandler;
    MockMvc mockMvc;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void beforeEach() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).setControllerAdvice(new ExceptionAdvice(responseHandler)).build();
    }

    @Test
    void repaymentPaymentNotFoundExceptionTest() throws Exception {
        Long id = 1L;
        doThrow(new PaymentNotFoundException()).when(paymentService).repayment(id);

        mockMvc.perform(post("/api/payments/v1/repayment/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void repaymentPaymentExecutionFailureExceptionTest() throws Exception {
        Long id = 1L;
        doThrow(new PaymentExecutionFailureException()).when(paymentService).repayment(id);

        mockMvc.perform(post("/api/payments/v1/repayment/{id}", id))
                .andExpect(status().isInternalServerError());
    }
}
