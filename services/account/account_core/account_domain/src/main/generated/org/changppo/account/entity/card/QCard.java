package org.changppo.account.entity.card;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QCard is a Querydsl query type for Card
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QCard extends EntityPathBase<Card> {

    private static final long serialVersionUID = 1699692686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QCard card = new QCard("card");

    public final org.changppo.account.entity.common.QEntityDate _super = new org.changppo.account.entity.common.QEntityDate(this);

    public final StringPath acquirerCorporation = createString("acquirerCorporation");

    public final StringPath bin = createString("bin");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath issuerCorporation = createString("issuerCorporation");

    public final StringPath key = createString("key");

    public final org.changppo.account.entity.member.QMember member;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> modifiedAt = _super.modifiedAt;

    public final QPaymentGateway paymentGateway;

    public final StringPath type = createString("type");

    public QCard(String variable) {
        this(Card.class, forVariable(variable), INITS);
    }

    public QCard(Path<? extends Card> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QCard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QCard(PathMetadata metadata, PathInits inits) {
        this(Card.class, metadata, inits);
    }

    public QCard(Class<? extends Card> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new org.changppo.account.entity.member.QMember(forProperty("member")) : null;
        this.paymentGateway = inits.isInitialized("paymentGateway") ? new QPaymentGateway(forProperty("paymentGateway")) : null;
    }

}

