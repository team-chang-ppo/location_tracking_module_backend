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
        Long totalCost,
        Long totalCount
) {

    public ApiKeyChargeView(Long apiKey, List<DayChargeView> dayCharges, Long totalCost, Long totalCount) {
        this.apiKey = apiKey;
        this.dayCharges = Collections.unmodifiableList(dayCharges);
        this.totalCost = dayCharges.stream().map(DayChargeView::totalCost).reduce(0L, Long::sum);
        this.totalCount = dayCharges.stream().map(DayChargeView::totalCount).reduce(0L, Long::sum);
    }

    public ApiKeyChargeView(Long apiKey, List<DayChargeView> dayCharges) {
        this(apiKey, dayCharges, 0L, 0L);
    }

}
