package org.changppo.monioring.domain.view;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 일별 요금 정보
 * @param date 날짜, UTC+0 기준 never null
 * @param hours 요금 never null
 */
public record DayChargeView(
        LocalDate date,
        List<HourChargeView> hours,
        Long totalCount,
        Long totalCost
) {
    public DayChargeView(LocalDate date, List<HourChargeView> hours, Long totalCount, Long totalCost) {
        this.date = date;
        this.hours = Collections.unmodifiableList(hours);
        this.totalCount = hours.stream().map(HourChargeView::count).reduce(0L, Long::sum);
        this.totalCost = hours.stream().map(HourChargeView::cost).reduce(0L, Long::sum);
    }

    public DayChargeView(LocalDate date, List<HourChargeView> hours) {
        this(date, hours, 0L, 0L);
    }
}