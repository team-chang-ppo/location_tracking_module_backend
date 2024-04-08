
package org.changppo.cost_management_service.entity.card;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "method_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private PaymentGatewayType paymentGatewayType;

    public PaymentGateway(PaymentGatewayType paymentGatewayType) {
        this.paymentGatewayType = paymentGatewayType;
    }
}
