package org.changppo.account.builder.billing;

import org.changppo.account.payment.dto.BillingInfoResponse;

import java.math.BigDecimal;

public class BillingInfoResponseBuilder {
    public static BillingInfoResponse buildBillingInfoResponse(Long totalCount, BigDecimal totalCost) {
        BillingInfoResponse.BillingResult billingResult = new BillingInfoResponse.BillingResult(totalCount, totalCost);
        return new BillingInfoResponse(true, billingResult);
    }
}
