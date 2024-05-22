package org.changppo.monioring.domain.view;

import java.util.List;

public record HourChargeView(
        Integer hour,
        List<ApiEndpointChargeDetailsView> apiEndpointDetails,
        Long amount
) {
    public HourChargeView(Integer hour, List<ApiEndpointChargeDetailsView> apiEndpointDetails, Long amount) {
        this.hour = hour;
        this.apiEndpointDetails = apiEndpointDetails;
        this.amount = apiEndpointDetails.stream()
                .mapToLong(ApiEndpointChargeDetailsView::cost)
                .sum();
    }

    public HourChargeView(Integer hour, List<ApiEndpointChargeDetailsView> apiEndpointDetails) {
        this(hour, apiEndpointDetails, apiEndpointDetails.stream()
                .mapToLong(ApiEndpointChargeDetailsView::cost)
                .sum());
    }
}
