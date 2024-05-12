package org.changppo.monioring.domain.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * API 키별 일별 요금 정보
 * @param apiKey API 키 never null
 * @param charges 일별 요금 정보 never null
 */
public record ApiKeyDayChargeView (
        Long apiKey,
        List<DateChargeView> charges
) {

    public ApiKeyDayChargeView(Long apiKey, List<DateChargeView> charges) {
        this.apiKey = apiKey;
        this.charges = Collections.unmodifiableList(charges);
    }

    @JsonProperty("totalAmount")
    public BigDecimal totalAmount() {
        return charges.stream()
                .map(DateChargeView::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
