package org.changppo.account.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.payment.dto.BillingInfoResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Component
@Slf4j
@EnableConfigurationProperties(BillingInfoProperties.class)
@RequiredArgsConstructor
public class BillingInfoClient {

    private final RestTemplate restTemplate;
    private final BillingInfoProperties billingInfoProperties;

    public static final String BILLING_INFO_URL_TEMPLATE = "/private/aggregation/v1/member/%d/charge/total";

    public ClientResponse<BillingInfoResponse> getBillingAmountForPeriod(Long memberId, LocalDate startDate, LocalDate endDate) {
        try {
            String url = buildUrl(memberId, startDate, endDate);
            BillingInfoResponse response = restTemplate.getForObject(url, BillingInfoResponse.class);
            handleResponse(response);
            return ClientResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to get billing amount for period. Member ID: {}, Start Date: {}, End Date: {}", memberId, startDate, endDate, e);
            return ClientResponse.failure();
        }
    }

    private String buildUrl(Long memberId, LocalDate startDate, LocalDate endDate) {
        return UriComponentsBuilder.fromHttpUrl(billingInfoProperties.getUrl() + String.format(BILLING_INFO_URL_TEMPLATE, memberId))
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString())
                .toUriString();
    }

    private void handleResponse(BillingInfoResponse response) {
        Assert.notNull(response, "Failed to get billing info response: Response is null");
        Assert.notNull(response.getResult(), "Result cannot be null");
        Assert.notNull(response.getResult().getTotalCount(), "Total count cannot be null");
        Assert.notNull(response.getResult().getTotalCost(), "Total cost cannot be null");
    }
}
