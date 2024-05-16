package org.changppo.account.builder.card.paymentgateway.kakaopay;

import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterApproveRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterCancelRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterFailRequest;
import org.changppo.account.dto.card.kakaopay.KakaopayCardRegisterReadyRequest;

public class KakaopayRequestBuilder {

    public static KakaopayCardRegisterReadyRequest buildKakaopayCardRegisterReadyRequest(Long memberId) {
        return new KakaopayCardRegisterReadyRequest(memberId);
    }
    public static KakaopayCardRegisterApproveRequest buildKakaopayCardRegisterApproveRequest() {
        return new KakaopayCardRegisterApproveRequest("partner_order_id_value", "pg_token_value", null);
    }
    public static KakaopayCardRegisterCancelRequest buildKakaopayCardRegisterCancelRequest() {
        return new KakaopayCardRegisterCancelRequest("partner_order_id_value",null);
    }
    public static KakaopayCardRegisterFailRequest buildKakaopayCardRegisterFailRequest() {
        return new KakaopayCardRegisterFailRequest("partner_order_id_value", null);
    }
}
