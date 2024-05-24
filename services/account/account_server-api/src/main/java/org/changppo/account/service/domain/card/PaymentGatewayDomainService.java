package org.changppo.account.service.domain.card;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.paymentgateway.PaymentGatewayClient;
import org.changppo.account.repository.card.PaymentGatewayRepository;
import org.changppo.account.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.account.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.account.type.PaymentGatewayType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class PaymentGatewayDomainService {

    private final PaymentGatewayRepository paymentGatewayRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    public PaymentGateway getPaymentGatewayByType(PaymentGatewayType paymentGatewayType) {
        return paymentGatewayRepository.findByPaymentGatewayType(paymentGatewayType).orElseThrow(PaymentGatewayNotFoundException::new);
    }

    public void inactivateCard(String cardKey, PaymentGatewayType paymentGatewayType) {
        paymentGatewayClients.stream()
                .filter(client -> client.supports(paymentGatewayType))
                .findFirst()
                .orElseThrow(UnsupportedPaymentGatewayException::new)
                .inactive(cardKey);
    }
}
