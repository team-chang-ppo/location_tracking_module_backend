package org.changppo.account.service.dto.apikey;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * org.changppo.account.service.dto.apikey.QApiKeyDto is a Querydsl Projection type for ApiKeyDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QApiKeyDto extends ConstructorExpression<ApiKeyDto> {

    private static final long serialVersionUID = -1864356924L;

    public QApiKeyDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> value, com.querydsl.core.types.Expression<org.changppo.account.type.GradeType> grade, com.querydsl.core.types.Expression<java.time.LocalDateTime> paymentFailureBannedAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> cardDeletionBannedAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt) {
        super(ApiKeyDto.class, new Class<?>[]{long.class, String.class, org.changppo.account.type.GradeType.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, id, value, grade, paymentFailureBannedAt, cardDeletionBannedAt, createdAt);
    }

}

