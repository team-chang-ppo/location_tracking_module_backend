package org.changppo.account.service.dto.member;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.ConstructorExpression;
import javax.annotation.processing.Generated;

/**
 * org.changppo.account.service.dto.member.QMemberDto is a Querydsl Projection type for MemberDto
 */
@Generated("com.querydsl.codegen.DefaultProjectionSerializer")
public class QMemberDto extends ConstructorExpression<MemberDto> {

    private static final long serialVersionUID = -784235196L;

    public QMemberDto(com.querydsl.core.types.Expression<Long> id, com.querydsl.core.types.Expression<String> name, com.querydsl.core.types.Expression<String> username, com.querydsl.core.types.Expression<String> profileImage, com.querydsl.core.types.Expression<? extends java.util.Set<org.changppo.account.type.RoleType>> roles, com.querydsl.core.types.Expression<java.time.LocalDateTime> paymentFailureBannedAt, com.querydsl.core.types.Expression<java.time.LocalDateTime> createdAt) {
        super(MemberDto.class, new Class<?>[]{long.class, String.class, String.class, String.class, java.util.Set.class, java.time.LocalDateTime.class, java.time.LocalDateTime.class}, id, name, username, profileImage, roles, paymentFailureBannedAt, createdAt);
    }

}

