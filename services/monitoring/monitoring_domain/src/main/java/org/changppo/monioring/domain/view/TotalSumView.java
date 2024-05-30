package org.changppo.monioring.domain.view;

public record TotalSumView(
        Long totalCount,
        Long totalCost
) {

    public static TotalSumView empty() {
        return new TotalSumView(0L, 0L);
    }
}
