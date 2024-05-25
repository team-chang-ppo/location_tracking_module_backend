package org.changppo.monioring.domain.view;

import java.util.Collections;
import java.util.List;

public record MemberChargeGraphView(
        Long memberId,
        List<ApiKeyChargeView> apiKeys,
        Long totalCost,
        Long totalCount
) {

    public MemberChargeGraphView(Long memberId, List<ApiKeyChargeView> apiKeys, Long totalCost, Long totalCount) {
        this.memberId = memberId;
        this.apiKeys = Collections.unmodifiableList(apiKeys);
        this.totalCost = apiKeys.stream().map(ApiKeyChargeView::totalCost).reduce(0L, Long::sum);
        this.totalCount = apiKeys.stream().map(ApiKeyChargeView::totalCount).reduce(0L, Long::sum);
    }

    public MemberChargeGraphView(Long memberId, List<ApiKeyChargeView> apiKeys) {
        this(memberId, apiKeys, 0L, 0L);
    }

    public static MemberChargeGraphView empty(Long memberId) {
        return new MemberChargeGraphView(memberId, Collections.emptyList());
    }
}
