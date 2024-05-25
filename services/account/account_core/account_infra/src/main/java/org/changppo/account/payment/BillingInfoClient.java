package org.changppo.account.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.BillingInfoResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@EnableConfigurationProperties(BillingInfoProperties.class)
@RequiredArgsConstructor
public class BillingInfoClient {

    private final RestTemplate restTemplate;
    private final BillingInfoProperties billingInfoProperties;

    private static final String BILLING_INFO_URL_TEMPLATE = "/private/aggregation/v1/member/%d/charge";

    public ClientResponse<BillingInfoResponse> getBillingAmountForPeriod(Long memberId, LocalDate startDate, LocalDate endDate) {
        try {
            HttpEntity<Map<String, Object>> request = createRequest(startDate, endDate);
            BillingInfoResponse response = restTemplate.postForObject(
                    String.format(billingInfoProperties.getUrl() + BILLING_INFO_URL_TEMPLATE, memberId),
                    request,
                    BillingInfoResponse.class
            );
            handleResponse(response);
            return ClientResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to get billing amount for period. Member ID: {}, Start Date: {}, End Date: {}", memberId, startDate, endDate, e);
            return ClientResponse.failure();
        }
    }

    private HttpEntity<Map<String, Object>> createRequest(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("startDate", startDate.format(DateTimeFormatter.ISO_DATE));
        parameters.put("endDate", endDate.format(DateTimeFormatter.ISO_DATE));
        HttpHeaders headers = getHeaders();
        return new HttpEntity<>(parameters, headers);
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

    private void handleResponse(BillingInfoResponse response) {
        Assert.notNull(response, "Failed to get billing info response: Response is null");
        Assert.notNull(response.getTotalCount(), "Total count cannot be null");
        Assert.notNull(response.getTotalAmount(), "Total amount cannot be null");
    }
}
