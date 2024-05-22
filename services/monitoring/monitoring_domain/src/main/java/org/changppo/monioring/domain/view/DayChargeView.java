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
        Long totalAmount
) {
    public DayChargeView(LocalDate date, List<HourChargeView> hours, Long totalAmount) {
        this.date = date;
        this.hours = Collections.unmodifiableList(hours);
        Long calculatedTotalAmount = 0L;
        for (HourChargeView hourChargeView : hours) {
            calculatedTotalAmount += hourChargeView.amount();
        }
        this.totalAmount = calculatedTotalAmount;
    }

    public DayChargeView(LocalDate date, List<HourChargeView> hours) {
        this(date, hours, 0L);
    }
}