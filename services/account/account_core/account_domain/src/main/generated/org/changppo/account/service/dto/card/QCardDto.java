package org.changppo.account.service.dto.card;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * org.changppo.account.service.dto.card.QCardDto is a Querydsl Projection type for CardDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QCardDto extends ConstructorExpression<CardDto> {

    private static final long serialVersionUID = -595286012L;

    public QCardDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> type, com.querydsl.core.types.Expression<String> issuerCorporation, com.querydsl.core.types.Expression<String> bin, com.querydsl.core.types.Expression<org.changppo.account.type.PaymentGatewayType> paymentGateway, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt) {
        super(CardDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, org.changppo.account.type.PaymentGatewayType.class, java.time.LocalDateTime.class}, id, type, issuerCorporation, bin, paymentGateway, createdAt);
    }

}

