package org.changppo.account.repository.card;

import org.changppo.account.card.PaymentGateway;
import org.changppo.account.type.PaymentGatewayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long> {
    Optional<PaymentGateway> findByPaymentGatewayType(PaymentGatewayType paymentGatewayType);
}