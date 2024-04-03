package org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayCancelRequest {
    private String partnerOrderId;
    private Long partnerUserId;
}
