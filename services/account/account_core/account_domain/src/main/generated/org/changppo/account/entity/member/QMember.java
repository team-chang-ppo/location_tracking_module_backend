package org.changppo.account.entity.member;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1990580686L;

    public static final QMember member = new QMember("member1");

    public final org.changppo.account.entity.common.QEntityDate _super = new org.changppo.account.entity.common.QEntityDate(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletionRequestedAt = createDateTime("deletionRequestedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final SetPath<MemberRole, QMemberRole> memberRoles = this.<MemberRole, QMemberRole>createSet("memberRoles", MemberRole.class, QMemberRole.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final StringPath name = createString("name");

    public final DateTimePath<java.time.LocalDateTime> paymentFailureBannedAt = createDateTime("paymentFailureBannedAt", java.time.LocalDateTime.class);

    public final StringPath profileImage = createString("profileImage");

    public final StringPath username = createString("username");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

