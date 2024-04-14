package org.changppo.monioring.domain.view;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 일별 요금 정보
 * @param date 날짜, UTC+0 기준 never null
 * @param amount 요금 never null
 */
public record DateChargeView(
        LocalDate date,
        BigDecimal amount
) {
}