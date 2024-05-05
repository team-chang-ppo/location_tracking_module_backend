package org.changppo.account.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.paymentgateway.dto.PaymentResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class PaymentExecutionJobClient {  //TODO. Spring Cloud Feign Client로 변경

    private final RestTemplate restTemplate;

    public ClientResponse<PaymentResponse> PaymentExecutionJob(PaymentExecutionJobRequest req) {
        try {
            HttpEntity<PaymentExecutionJobRequest> request = createPaymentExecutionJobRequest(req);
            PaymentResponse paymentResponse = restTemplate.postForObject(
                    "http://localhost:8081/batch/executePayment",
                    request,
                    PaymentResponse.class
            );
            handleResponse(paymentResponse);
            validatePaymentExecutionJobResponse(paymentResponse);
            return ClientResponse.success(paymentResponse);
        } catch (Exception e) {
            log.info("Failed to process payment execution for User ID: {}", req.getMemberId(), e);
            return ClientResponse.failure();
        }
    }

    public static <T> void handleResponse(T response) {
        if (response == null) {
            throw new RuntimeException("Failed to process PaymentExecutionJob Response: Response is null");
        }
    }

    private void validatePaymentExecutionJobResponse(PaymentResponse response) {
        if (response.getKey() == null || response.getCardType() == null || response.getCardIssuerCorporation() == null || response.getCardBin() == null) {
            throw new IllegalStateException("PaymentExecutionJobResponse cannot be null.");
        }
    }

    private HttpEntity<PaymentExecutionJobRequest> createPaymentExecutionJobRequest(PaymentExecutionJobRequest req) {
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(req, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

}
