package org.changppo.account.paymentgateway.kakaopay.dto.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

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