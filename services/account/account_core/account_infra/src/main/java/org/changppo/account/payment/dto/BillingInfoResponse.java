package org.changppo.account.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingInfoResponse {
    private boolean success;
    private BillingResult result;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BillingResult {
        private Long totalCount;
        private BigDecimal totalCost;
    }
}
