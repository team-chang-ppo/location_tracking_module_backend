package org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayReadyRequest {
    private String cid;
    private String partnerOrderId;
    private Long partnerUserId;
    private String itemName;
    private int quantity;
    private int totalAmount;
    private int taxFreeAmount;
    private String approvalUrl;
    private String cancelUrl;
    private String failUrl;
}
