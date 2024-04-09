package org.changppo.cost_management_service.builder.card;

import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterCancelRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.cost_management_service.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;

public class KakaopayRequestBuilder {

    public static KakaopayCardRegisterReadyRequest buildKakaopayCardRegisterReadyRequest(Long memberId) {
        return new KakaopayCardRegisterReadyRequest(memberId);
    }
    public static KakaopayCardRegisterApproveRequest buildKakaopayCardRegisterApproveRequest() {
        return new KakaopayCardRegisterApproveRequest("partner_order_id_value", "pg_token_value", null);
    }
    public static KakaopayCardRegisterCancelRequest buildKakaopayCardRegisterCancelRequest() {
        return new KakaopayCardRegisterCancelRequest("partner_order_id_value");
    }
    public static KakaopayCardRegisterFailRequest buildKakaopayCardRegisterFailRequest() {
        return new KakaopayCardRegisterFailRequest("partner_order_id_value", null);
    }
}
