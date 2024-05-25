package org.changppo.account.repository.card;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.response.exception.card.PaymentGatewayNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.changppo.account.type.PaymentGatewayType.PG_KAKAOPAY;

@DataJpaTest
@Import(QuerydslConfig.class)
class PaymentGatewayRepositoryTest {

    @Autowired
    PaymentGatewayRepository paymentGatewayRepository;
    @PersistenceContext
    EntityManager em;

    @Test
    void createTest() {
        // given
        PaymentGateway paymentGateway = paymentGatewayRepository.save(buildPaymentGateway(PG_KAKAOPAY));

        // when
        paymentGatewayRepository.save(paymentGateway);
        clear();

        // then
        PaymentGateway foundPaymentGateway = paymentGatewayRepository.findById(paymentGateway.getId()).orElseThrow(PaymentGatewayNotFoundException::new);
        assertThat(foundPaymentGateway.getId()).isEqualTo(paymentGateway.getId());
    }

    @Test
    void deleteTest() {
        // given
        PaymentGateway paymentGateway = paymentGatewayRepository.save(buildPaymentGateway(PG_KAKAOPAY));
        clear();

        // when
        paymentGatewayRepository.delete(paymentGateway);

        // then
        assertThatThrownBy(() -> paymentGatewayRepository.findById(paymentGateway.getId()).orElseThrow(PaymentGatewayNotFoundException::new))
                .isInstanceOf(PaymentGatewayNotFoundException.class);
    }

    @Test
    void uniqueGradeTypeTest() {
        // given
        paymentGatewayRepository.save(buildPaymentGateway(PG_KAKAOPAY));
        clear();

        // when, then
        assertThatThrownBy(() -> paymentGatewayRepository.save(buildPaymentGateway(PG_KAKAOPAY)))
                .isInstanceOf(DataIntegrityViolationException.class);

    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
