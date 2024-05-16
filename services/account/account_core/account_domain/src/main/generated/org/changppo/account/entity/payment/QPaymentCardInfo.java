package org.changppo.account.entity.payment;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QPaymentCardInfo is a Querydsl query type for PaymentCardInfo
 */
@Generated("com.querydsl.codegen.DefaultEmbeddableSerializer")
public class QPaymentCardInfo extends BeanPath<PaymentCardInfo> {

    private static final long serialVersionUID = 592081212L;

    public static final QPaymentCardInfo paymentCardInfo = new QPaymentCardInfo("paymentCardInfo");

    public final StringPath bin = createString("bin");

    public final StringPath issuerCorporation = createString("issuerCorporation");

    public final StringPath type = createString("type");

    public QPaymentCardInfo(String variable) {
        super(PaymentCardInfo.class, forVariable(variable));
    }

    public QPaymentCardInfo(Path<? extends PaymentCardInfo> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPaymentCardInfo(PathMetadata metadata) {
        super(PaymentCardInfo.class, metadata);
    }

}

