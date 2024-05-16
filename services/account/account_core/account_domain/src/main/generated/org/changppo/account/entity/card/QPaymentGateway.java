package org.changppo.account.entity.card;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentGateway is a Querydsl query type for PaymentGateway
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPaymentGateway extends EntityPathBase<PaymentGateway> {

    private static final long serialVersionUID = 2090858844L;

    public static final QPaymentGateway paymentGateway = new QPaymentGateway("paymentGateway");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<org.changppo.account.type.PaymentGatewayType> paymentGatewayType = createEnum("paymentGatewayType", org.changppo.account.type.PaymentGatewayType.class);

    public QPaymentGateway(String variable) {
        super(PaymentGateway.class, forVariable(variable));
    }

    public QPaymentGateway(Path<? extends PaymentGateway> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentGateway(PathMetadata metadata) {
        super(PaymentGateway.class, metadata);
    }

}

