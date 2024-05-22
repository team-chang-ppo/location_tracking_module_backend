package org.changppo.monioring.domain.view;

import java.util.Collections;
import java.util.List;

/**
 * API 키별 일별 요금 정보
 * @param apiKey API 키 never null
 * @param dayCharges 일별 요금 never null
 */
public record ApiKeyChargeView(
        Long apiKey,
        List<DayChargeView> dayCharges,
        Long totalAmount
) {

    public ApiKeyChargeView(Long apiKey, List<DayChargeView> dayCharges, Long totalAmount) {
        this.apiKey = apiKey;
        this.dayCharges = Collections.unmodifiableList(dayCharges);
        Long calculatedTotalAmount = 0L;
        for (DayChargeView dayChargeView : dayCharges) {
            calculatedTotalAmount += dayChargeView.totalAmount();
        }
        this.totalAmount = calculatedTotalAmount;
    }

    public ApiKeyChargeView(Long apiKey,List<DayChargeView> dayCharges) {
        this(apiKey, dayCharges, 0L);
    }
}
