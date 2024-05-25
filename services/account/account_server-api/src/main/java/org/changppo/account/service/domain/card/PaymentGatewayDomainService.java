package org.changppo.account.service.domain.card;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.repository.card.PaymentGatewayRepository;
import org.changppo.account.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.account.type.PaymentGatewayType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class PaymentGatewayDomainService {

    private final PaymentGatewayRepository paymentGatewayRepository;

    public PaymentGateway getPaymentGatewayByType(PaymentGatewayType paymentGatewayType) {
        return paymentGatewayRepository.findByPaymentGatewayType(paymentGatewayType).orElseThrow(PaymentGatewayNotFoundException::new);
    }
}
