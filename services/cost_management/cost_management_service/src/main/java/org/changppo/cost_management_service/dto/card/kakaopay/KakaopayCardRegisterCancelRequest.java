package org.changppo.cost_management_service.dto.card.kakaopay;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaopayCardRegisterCancelRequest {
    @NotNull(message = "{kakaopayCardRegisterCancelRequest.partnerOrderId.notNull}")
    private String partner_order_id;
}
