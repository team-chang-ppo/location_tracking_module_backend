package org.changppo.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.config.BatchServerUrlProperties;
import org.changppo.account.batch.dto.PaymentExecutionJobRequest;
import org.changppo.account.batch.dto.PaymentExecutionJobResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Component
@Slf4j
@EnableConfigurationProperties(BatchServerUrlProperties.class)
public class PaymentExecutionJobClient {

    private final RestTemplate restTemplate;
    private final BatchServerUrlProperties batchServerUrlProperties;

    public ClientResponse<PaymentExecutionJobResponse> PaymentExecutionJob(PaymentExecutionJobRequest req) {
        try {
            HttpEntity<PaymentExecutionJobRequest> request = createPaymentExecutionJobRequest(req);
            PaymentExecutionJobResponse paymentExecutionJobResponse = restTemplate.postForObject(
                    batchServerUrlProperties.getUrl() + "/batch/executePayment",
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
