package org.changppo.account.builder.card;

import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.service.dto.card.CardDto;

import java.time.LocalDateTime;

public class CardDtoBuilder {
    public static CardDto buildCardDto(PaymentGateway paymentGateway) {
        return new CardDto(1L, "Credit", "IssuerCorp", "123456", paymentGateway.getPaymentGatewayType(), LocalDateTime.now());

    }
}
