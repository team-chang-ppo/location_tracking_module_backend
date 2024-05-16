package org.changppo.account.builder.card.paymentgateway.kakaopay;


import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.paymentgateway.kakaopay.dto.subscription.KakaopaySubscriptionInactiveResponse;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.*;


public class KakaopayResponseBuilder {

    public static KakaopayApproveResponse buildKakaopayApproveResponse(Long memberId, LocalDateTime time) {
        String aid = "aid_" + UUID.randomUUID();
        String tid = "tid_" + UUID.randomUUID();
        String cid = "cid_" + UUID.randomUUID();
        String sid = "sid_" + UUID.randomUUID();
        String partnerOrderId = "partnerOrderId_" + UUID.randomUUID();
        String partnerUserId = memberId.toString();
        String paymentMethodType = "CARD";
        KakaopayApproveResponse.Amount amount = new KakaopayApproveResponse.Amount(0, 0, 0, 0, 0, 0);
        KakaopayApproveResponse.CardInfo cardInfo = new KakaopayApproveResponse.CardInfo("Kakaopay Corp", "123", "Issuer Corp", "456", "123456", "CREDIT", "0", "approvedId", "cardMid", "N", "FULL", "itemCode");
        String itemName = "Test Item";
        String itemCode = "item_" + UUID.randomUUID().toString();
        int quantity = 1;
        String createdAt = time.toString();
        String approvedAt = time.toString();
        String payload = "test";

        return new KakaopayApproveResponse(
                aid, tid, cid, sid, partnerOrderId, partnerUserId,
                paymentMethodType, amount, cardInfo, itemName, itemCode, quantity,
                createdAt, approvedAt, payload
        );
    }

    public static KakaopaySubscriptionInactiveResponse buildKakaopaySubscriptionInactiveResponse(LocalDateTime time) {
        return new KakaopaySubscriptionInactiveResponse(
                CID,
                SID,
                INACTIVE,
                time.toString(),
                time.toString(),
                time.toString()
        );
    }
}
