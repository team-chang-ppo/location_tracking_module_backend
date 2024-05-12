
package org.changppo.account.entity.card;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.account.type.PaymentGatewayType;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_gateway_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PaymentGatewayType paymentGatewayType;

    public PaymentGateway(PaymentGatewayType paymentGatewayType) {
        this.paymentGatewayType = paymentGatewayType;
    }
}
