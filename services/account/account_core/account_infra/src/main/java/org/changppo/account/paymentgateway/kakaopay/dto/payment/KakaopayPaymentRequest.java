package org.changppo.account.paymentgateway.kakaopay.dto.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayPaymentRequest {
    private String sid;
    private String partnerOrderId;
    private Long partnerUserId;
    private String itemName;
    private int quantity;
    private int totalAmount;
    private int taxFreeAmount;
}
