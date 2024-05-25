package org.changppo.monioring.domain.view;

import java.util.List;

public record HourChargeView(
        Integer hour,
        Long cost,
        Long count
) {
    public HourChargeView(Integer hour, Long cost, Long count) {
        this.hour = hour;
        this.cost = cost;
        this.count = count;
    }
}
