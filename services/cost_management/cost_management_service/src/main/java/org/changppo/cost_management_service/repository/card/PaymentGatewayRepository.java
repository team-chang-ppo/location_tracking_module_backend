package org.changppo.cost_management_service.repository.card;

import org.changppo.cost_management_service.entity.card.PaymentGateway;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGateway, Long> {
    Optional<PaymentGateway> findByPaymentGatewayType(PaymentGatewayType paymentGatewayType);
}