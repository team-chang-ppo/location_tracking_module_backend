package org.changppo.account.payment.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BillingInfoResponse {
    private Long memberId;
    private List<ApiKeyInfo> apiKeys;
    private BigDecimal totalAmount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiKeyInfo {
        private Long apiKey;
        private List<DayCharge> dayCharges;
        private BigDecimal totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DayCharge {
        private String date;
        private List<HourCharge> hours;
        private BigDecimal totalAmount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class HourCharge {
        private Integer hour;
        private List<ApiEndpointDetail> apiEndpointDetails;
        private BigDecimal amount;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiEndpointDetail {
        private Long apiEndpointId;
        private Integer count;
        private BigDecimal costPerCount;
        private BigDecimal cost;
    }
}
