package org.changppo.account.paymentgateway.kakaopay.dto.payment;

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
