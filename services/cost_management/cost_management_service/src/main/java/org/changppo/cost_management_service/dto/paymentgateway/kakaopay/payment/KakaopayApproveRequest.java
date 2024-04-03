package org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayApproveRequest {
    private String cid;
    private String partnerOrderId;
    private Long partnerUserId;
    private String pgToken;
}
