package org.changppo.cost_management_service.dto.paymentgateway.kakaopay.subscription;

import lombok.*;

@Data
@AllArgsConstructor
public class KakaopaySubscriptionInactiveResponse {
    private String cid;
    private String sid;
    private String status;
    private String created_at;
    private String last_approved_at;
    private String inactivated_at;
}