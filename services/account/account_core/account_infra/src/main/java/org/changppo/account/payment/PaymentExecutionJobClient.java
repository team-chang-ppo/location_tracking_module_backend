package org.changppo.account.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.PaymentExecutionJobRequest;
import org.changppo.account.payment.dto.PaymentExecutionJobResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
@EnableConfigurationProperties(PaymentExecutionJobProperties.class)
public class PaymentExecutionJobClient {  //TODO. Spring Cloud Feign Client로 변경

    private final RestTemplate restTemplate;
    private final PaymentExecutionJobProperties paymentExecutionJobProperties;

    public ClientResponse<PaymentExecutionJobResponse> PaymentExecutionJob(PaymentExecutionJobRequest req) {
        try {
            HttpEntity<PaymentExecutionJobRequest> request = createPaymentExecutionJobRequest(req);
            PaymentExecutionJobResponse paymentExecutionJobResponse = restTemplate.postForObject(
                    paymentExecutionJobProperties.getUrl() + "/batch/executePayment",
                    request,
                    PaymentExecutionJobResponse.class
            );
            handleResponse(paymentExecutionJobResponse);
            validatePaymentExecutionJobResponse(paymentExecutionJobResponse);
            return ClientResponse.success(paymentExecutionJobResponse);
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

    private void validatePaymentExecutionJobResponse(PaymentExecutionJobResponse response) {
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
