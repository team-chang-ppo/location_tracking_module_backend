package org.changppo.monioring.domain.view;

import java.util.Collections;
import java.util.List;

public record MemberChargeGraphView(
        Long memberId,
        List<ApiKeyChargeView> apiKeys,
        Long totalAmount
) {

    public MemberChargeGraphView(Long memberId, List<ApiKeyChargeView> apiKeys, Long totalAmount) {
        this.memberId = memberId;
        this.apiKeys = Collections.unmodifiableList(apiKeys);
        Long calculatedTotalAmount = 0L;
        for (ApiKeyChargeView apiKeyChargeView : apiKeys) {
            calculatedTotalAmount += apiKeyChargeView.totalAmount();
        }
        this.totalAmount = calculatedTotalAmount;
    }

    public MemberChargeGraphView(Long memberId, List<ApiKeyChargeView> apiKeys) {
        this(memberId, apiKeys, 0L);
    }
}
