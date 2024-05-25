package org.changppo.account.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.batch.dto.PaymentExecutionJobRequest;
import org.changppo.account.batch.dto.PaymentExecutionJobResponse;
import org.changppo.account.config.BatchServerUrlProperties;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.springframework.util.Assert.notNull;

@RequiredArgsConstructor
@Component
@Slf4j
@EnableConfigurationProperties(BatchServerUrlProperties.class)
public class PaymentExecutionJobClient {

    private final RestTemplate restTemplate;
    private final BatchServerUrlProperties batchServerUrlProperties;

    public static final String PAYMENT_EXECUTION_JOB_URL = "/batch/executePayment";

    public ClientResponse<PaymentExecutionJobResponse> PaymentExecutionJob(PaymentExecutionJobRequest req) {
        try {
            HttpEntity<PaymentExecutionJobRequest> request = createPaymentExecutionJobRequest(req);
            PaymentExecutionJobResponse paymentExecutionJobResponse = restTemplate.postForObject(
                    batchServerUrlProperties.getUrl() + PAYMENT_EXECUTION_JOB_URL,
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

    public <T> void handleResponse(T response) {
        notNull(response, "Failed to process PaymentExecutionJob Response: Response is null");
    }

    private void validatePaymentExecutionJobResponse(PaymentExecutionJobResponse response) {
        notNull(response.getKey(), "PaymentExecutionJobResponse key cannot be null.");
        notNull(response.getCardType(), "PaymentExecutionJobResponse cardType cannot be null.");
        notNull(response.getCardIssuerCorporation(), "PaymentExecutionJobResponse cardIssuerCorporation cannot be null.");
        notNull(response.getCardBin(), "PaymentExecutionJobResponse cardBin cannot be null.");
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
