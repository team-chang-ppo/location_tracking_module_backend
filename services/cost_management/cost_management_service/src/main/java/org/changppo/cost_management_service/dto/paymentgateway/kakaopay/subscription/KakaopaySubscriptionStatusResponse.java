package org.changppo.cost_management_service.dto.paymentgateway.kakaopay.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaopaySubscriptionStatusResponse {
    private boolean available;
    private String cid;
    private String sid;
    private String status;
    private String payment_method_type;
    private String item_name;
    private String created_at;
    private String last_approved_at;
    private String inactivated_at;
    private String use_point_status;
}