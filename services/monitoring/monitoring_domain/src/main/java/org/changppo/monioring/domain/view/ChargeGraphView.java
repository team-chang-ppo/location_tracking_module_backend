package org.changppo.monioring.domain.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * @param currency 화폐 단위 (ISO 4217) never null
 * @param apiKeys API 키별 일별 요금 정보 never null
 */
public record ChargeGraphView(
        String currency,
        List<ApiKeyDayChargeView> apiKeys
) {

    public ChargeGraphView(String currency, List<ApiKeyDayChargeView> apiKeys) {
        this.currency = currency;
        this.apiKeys = Collections.unmodifiableList(apiKeys);
    }

    @JsonProperty("totalAmount")
    public BigDecimal totalAmount() {
        return apiKeys.stream()
                .map(ApiKeyDayChargeView::totalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
