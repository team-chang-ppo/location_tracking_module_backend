package org.changppo.account.entity.apikey;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QApiKey is a Querydsl query type for ApiKey
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QApiKey extends EntityPathBase<ApiKey> {

    private static final long serialVersionUID = -1553153202L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QApiKey apiKey = new QApiKey("apiKey");

    public final org.changppo.account.entity.common.QEntityDate _super = new org.changppo.account.entity.common.QEntityDate(this);

    public final DateTimePath<java.time.LocalDateTime> adminBannedAt = createDateTime("adminBannedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> cardDeletionBannedAt = createDateTime("cardDeletionBannedAt", java.time.LocalDateTime.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> deletionRequestedAt = createDateTime("deletionRequestedAt", java.time.LocalDateTime.class);

    public final QGrade grade;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final org.changppo.account.entity.member.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final DateTimePath<java.time.LocalDateTime> paymentFailureBannedAt = createDateTime("paymentFailureBannedAt", java.time.LocalDateTime.class);

    public final StringPath value = createString("value");

    public QApiKey(String variable) {
        this(ApiKey.class, forVariable(variable), INITS);
    }

    public QApiKey(Path<? extends ApiKey> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QApiKey(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QApiKey(PathMetadata metadata, PathInits inits) {
        this(ApiKey.class, metadata, inits);
    }

    public QApiKey(Class<? extends ApiKey> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.grade = inits.isInitialized("grade") ? new QGrade(forProperty("grade")) : null;
        this.member = inits.isInitialized("member") ? new org.changppo.account.entity.member.QMember(forProperty("member"), inits.get("member")) : null;
    }

}

