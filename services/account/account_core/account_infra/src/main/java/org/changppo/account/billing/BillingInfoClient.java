package org.changppo.account.billing;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.account.billing.dto.BillingInfoResponse;
import org.changppo.account.response.ClientResponse;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.time.LocalDate;

import static org.springframework.util.Assert.notNull;

@Component
@Slf4j
@EnableConfigurationProperties(BillingInfoProperties.class)
@RequiredArgsConstructor
public class BillingInfoClient {

    private final RestTemplate restTemplate;
    private final BillingInfoProperties billingInfoProperties;

    public static final String BILLING_INFO_URL_TEMPLATE = "/private/aggregation/v1/member/%d/charge/total";

    public ClientResponse<BillingInfoResponse> getBillingAmountForPeriod(Long memberId, LocalDate periodStart, LocalDate periodEnd) {
        try {
            BillingInfoResponse response = restTemplate.getForObject(
                    createUrl(memberId, periodStart, periodEnd),
                    BillingInfoResponse.class
            );
            handleResponse(response);
            validateBillingInfoResponse(response);
            return ClientResponse.success(response);
        } catch (Exception e) {
            log.error("Failed to get billing amount for period. Member ID: {}, Start Date: {}, End Date: {}", memberId, periodStart, periodEnd, e);
            return ClientResponse.failure();
        }
    }

    private String createUrl(Long memberId, LocalDate startDate, LocalDate endDate) {
        return UriComponentsBuilder.fromHttpUrl(billingInfoProperties.getUrl() + String.format(BILLING_INFO_URL_TEMPLATE, memberId))
                .queryParam("startDate", startDate.toString())
                .queryParam("endDate", endDate.toString())
                .toUriString();
    }

    private <T> void handleResponse(T response) {
        notNull(response, "Failed to get billing info response: Response is null");
    }

    private void validateBillingInfoResponse(BillingInfoResponse response) {
        notNull(response.getResult(), "Result cannot be null");
        notNull(response.getResult().getTotalCount(), "Total count cannot be null");
        notNull(response.getResult().getTotalCost(), "Total cost cannot be null");
    }
}
