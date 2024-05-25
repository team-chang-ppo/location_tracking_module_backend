package org.changppo.account.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.BillingInfoResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@EnableConfigurationProperties(BillingInfoProperties.class)
@RequiredArgsConstructor
public class BillingInfoClient {

    private final RestTemplate restTemplate;
    private final BillingInfoProperties billingInfoProperties;

    private static final String BILLING_INFO_URL_TEMPLATE = "/private/aggregation/v1/member/%d/charge";

    public ClientResponse<BigDecimal> getBillingAmountForPeriod(Long memberId, LocalDate startDate, LocalDate endDate, Long apiKeyId) {
        try {
            HttpEntity<Map<String, Object>> request = createRequest(startDate, endDate, apiKeyId);
            BillingInfoResponse response = restTemplate.postForObject(
                    String.format(billingInfoProperties.getUrl() + BILLING_INFO_URL_TEMPLATE, memberId),
                    request,
                    BillingInfoResponse.class
            );
            validateResponse(response);
            return ClientResponse.success(response.getTotalAmount());
        } catch (Exception e) {
            log.error("Failed to get billing amount for period. Member ID: {}, Start Date: {}, End Date: {}, API Key ID: {}", memberId, startDate, endDate, apiKeyId, e);
            return ClientResponse.failure();
        }
    }

    private HttpEntity<Map<String, Object>> createRequest(LocalDate startDate, LocalDate endDate, Long apiKeyId) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));
        parameters.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE));
        if (apiKeyId != null) {
            parameters.put("apiKeyId", apiKeyId);
        }
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void validateResponse(BillingInfoResponse response) {
        Assert.notNull(response, "Failed to get billing info response: Response is null");
        Assert.notNull(response.getTotalAmount(), "Total amount cannot be null");
        Assert.notEmpty(response.getApiKeys(), "API keys list cannot be empty");

        response.getApiKeys().forEach(apiKeyInfo -> {
            Assert.notNull(apiKeyInfo.getApiKey(), "API key cannot be null");
            Assert.notEmpty(apiKeyInfo.getDayCharges(), "Day charges list cannot be empty");

            apiKeyInfo.getDayCharges().forEach(dayCharge -> {
                Assert.hasText(dayCharge.getDate(), "Date cannot be empty");
                Assert.notEmpty(dayCharge.getHours(), "Hours list cannot be empty");

                dayCharge.getHours().forEach(hourCharge -> {
                    Assert.notEmpty(hourCharge.getApiEndpointDetails(), "API endpoint details list cannot be empty");

                    hourCharge.getApiEndpointDetails().forEach(apiEndpointDetail -> {
                        Assert.notNull(apiEndpointDetail.getApiEndpointId(), "API endpoint ID cannot be null");
                        Assert.isTrue(apiEndpointDetail.getCount() > 0, "Count must be greater than 0");
                        Assert.notNull(apiEndpointDetail.getCostPerCount(), "Cost per count cannot be null");
                        Assert.notNull(apiEndpointDetail.getCost(), "Cost cannot be null");
                    });
                });
            });
        });
    }
}
