package org.changppo.account.service.domain.card;

import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.repository.card.PaymentGatewayRepository;
import org.changppo.account.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.account.type.PaymentGatewayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class PaymentGatewayDomainServiceTest {

    @InjectMocks
    private PaymentGatewayDomainService paymentGatewayDomainService;

    @Mock
    private PaymentGatewayRepository paymentGatewayRepository;

    PaymentGateway paymentGateway;

    @BeforeEach
    void beforeEach() {
        paymentGateway = buildPaymentGateway(PaymentGatewayType.PG_KAKAOPAY);
    }

    @Test
    void getGradeByTypeTest() {
        // given
        given(paymentGatewayRepository.findByPaymentGatewayType(PaymentGatewayType.PG_KAKAOPAY)).willReturn(Optional.of(paymentGateway));

        // when
        PaymentGateway result = paymentGatewayDomainService.getPaymentGatewayByType(PaymentGatewayType.PG_KAKAOPAY);

        // then
        assertThat(result).isEqualTo(paymentGateway);
    }

    @Test
    void getGradeByTypeExceptionTest() {
        // given
        given(paymentGatewayRepository.findByPaymentGatewayType(PaymentGatewayType.PG_KAKAOPAY)).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> paymentGatewayDomainService.getPaymentGatewayByType(PaymentGatewayType.PG_KAKAOPAY)).isInstanceOf(PaymentGatewayNotFoundException.class);
    }
}
