package org.changppo.account.service.dto.payment;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * org.changppo.account.service.dto.payment.QPaymentDto is a Querydsl Projection type for PaymentDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QPaymentDto extends ConstructorExpression<PaymentDto> {

    private static final long serialVersionUID = 1164874004L;

    public QPaymentDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<? extends java.math.BigDecimal> amount, com.querydsl.core.types.Expression<org.changppo.account.type.PaymentStatus> status, com.querydsl.core.types.Expression<java.time.LocalDateTime> startedAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> endedAt, com.querydsl.core.types.Expression<? extends org.changppo.account.entity.payment.PaymentCardInfo> cardInfo, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt) {
        super(PaymentDto.class, new Class<?>[]{long.class, java.math.BigDecimal.class, org.changppo.account.type.PaymentStatus.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, org.changppo.account.entity.payment.PaymentCardInfo.class, java.time.LocalDateTime.class}, id, amount, status, startedAt, endedAt, cardInfo, createdAt);
    }

}

