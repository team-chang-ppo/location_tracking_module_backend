package org.changppo.account.builder.card;

import org.changppo.account.dto.card.CardCreateRequest;
import org.changppo.account.type.PaymentGatewayType;

public class CardCreateRequestBuilder {

    public static CardCreateRequest buildCardCreateRequest(Long memberId, PaymentGatewayType paymentGatewayType) {
        return new CardCreateRequest(
                "testKey",
                "testType",
                "testAcquirerCorporation",
                "testIssuerCorporation",
                "testBin",
                paymentGatewayType,
                memberId
        );
    }
}
