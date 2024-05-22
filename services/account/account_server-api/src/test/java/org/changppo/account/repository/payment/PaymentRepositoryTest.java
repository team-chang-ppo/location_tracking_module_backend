package org.changppo.account.repository.payment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.assertj.core.api.Assertions;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.entity.payment.Payment;
import org.changppo.account.entity.payment.PaymentCardInfo;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.payment.PaymentNotFoundException;
import org.changppo.account.service.dto.payment.PaymentDto;
import org.changppo.account.type.PaymentStatus;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.payment.PaymentBuilder.buildPayment;

@DataJpaTest
@Import(QuerydslConfig.class)
class PaymentRepositoryTest {

    @Autowired
    PaymentRepository paymentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @PersistenceContext
    EntityManager em;

    Role role;
    Member member;

    @BeforeEach
    void beforeEach() {
        role = roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
        member = memberRepository.save(buildMember(role));
    }

    @Test
    void createTest() {
        // given
        Payment payment = buildPayment(member);

        // when
        paymentRepository.save(payment);
        clear();

        // then
        Payment foundPayment = paymentRepository.findById(payment.getId()).orElseThrow(() -> new RuntimeException("Payment not found"));
        assertThat(foundPayment.getKey()).isEqualTo(payment.getKey());
        assertThat(foundPayment.getAmount()).isEqualTo(payment.getAmount());
        assertThat(foundPayment.getStatus()).isEqualTo(payment.getStatus());
        assertThat(foundPayment.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    void uniqueKeyTest() {
        // given
        paymentRepository.save(buildPayment(member));
        clear();

        // when, then
        assertThatThrownBy(() -> paymentRepository.save(buildPayment(member)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dateTest() {
        // given
        Payment payment = buildPayment(member);

        // when
        paymentRepository.save(payment);
        clear();

        // then
        Payment foundPayment = paymentRepository.findById(payment.getId()).orElseThrow(() -> new RuntimeException("Payment not found"));
        assertThat(foundPayment.getCreatedAt()).isNotNull();
        assertThat(foundPayment.getModifiedAt()).isNotNull();
        assertThat(foundPayment.getCreatedAt()).isEqualTo(foundPayment.getModifiedAt());
    }

    @Test
    void changeStatusTest() {
        // given
        Payment payment = paymentRepository.save(buildPayment(member));
        clear();

        // when
        Payment foundPayment = paymentRepository.findById(payment.getId()).orElseThrow(PaymentNotFoundException::new);
        foundPayment.changeStatus(PaymentStatus.COMPLETED_PAID, "newKey", new PaymentCardInfo("type", "issuerCorporation", "bin"));
        paymentRepository.save(foundPayment);
        clear();

        // then
        Payment updatedPayment = paymentRepository.findById(payment.getId()).orElseThrow(() -> new RuntimeException("Payment not found"));
        assertThat(updatedPayment.getStatus()).isEqualTo(PaymentStatus.COMPLETED_PAID);
        assertThat(updatedPayment.getKey()).isEqualTo(foundPayment.getKey());
        assertThat(updatedPayment.getCardInfo().getBin()).isEqualTo(foundPayment.getCardInfo().getBin());
        assertThat(updatedPayment.getCardInfo().getType()).isEqualTo(foundPayment.getCardInfo().getType());
        assertThat(updatedPayment.getCardInfo().getIssuerCorporation()).isEqualTo(foundPayment.getCardInfo().getIssuerCorporation());
    }

    @Test
    void deleteTest() {
        // given
        Payment payment = paymentRepository.save(buildPayment(member));
        clear();

        // when
        paymentRepository.delete(payment);
        clear();

        // then
        Assertions.assertThatThrownBy(() -> paymentRepository.findById(payment.getId()).orElseThrow(PaymentNotFoundException::new))
                .isInstanceOf(PaymentNotFoundException.class);
    }

    @Test
    void deleteAllByMemberId() {
        // given
        paymentRepository.save(buildPayment(member));
        clear();

        // when
        paymentRepository.deleteAllByMemberId(member.getId());
        clear();

        // then
        List<Payment> payments = paymentRepository.findAll();
        assertThat(payments.isEmpty()).isTrue();
    }

    @Test
    void findFirstByMemberIdOrderByEndedAtDesc() {
        // given
        Payment payment1 = paymentRepository.save(buildPayment("key1", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), member, new PaymentCardInfo("type", "issuerCorporation", "bin")));
        Payment payment2 = paymentRepository.save(buildPayment("key2", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), member, new PaymentCardInfo("type", "issuerCorporation", "bin")));
        clear();

        // when
        Payment foundPayment = paymentRepository.findFirstByMemberIdOrderByEndedAtDesc(member.getId()).orElseThrow(PaymentNotFoundException::new);

        // then
        assertThat(foundPayment.getKey()).isEqualTo(payment2.getKey());
    }

    @Test
    void findAllDtosByMemberIdAndStatusNotCompletedFree() {
        // given
        Payment payment1 = paymentRepository.save(buildPayment("key1", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), member, new PaymentCardInfo("type", "issuerCorporation", "bin")));
        Payment payment2 = paymentRepository.save(buildPayment(null, new BigDecimal("100.00"), PaymentStatus.COMPLETED_FREE, LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), member,null));
        clear();

        // when
        Slice<PaymentDto> paymentDtos = paymentRepository.findAllDtosByMemberIdAndStatusNotCompletedFree(member.getId(), null, PageableBuilder.build());

        // then
        assertThat(paymentDtos.getNumberOfElements()).isEqualTo(1);
        assertThat(paymentDtos.getContent().get(0).getId()).isEqualTo(payment1.getId());
    }

    @Test
    void findAllDtosTest() {
        // given
        paymentRepository.save(buildPayment("key1", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now(), LocalDateTime.now().plusMinutes(30), member, new PaymentCardInfo("type", "issuerCorporation", "bin")));
        paymentRepository.save(buildPayment("key2", new BigDecimal("100.00"), PaymentStatus.COMPLETED_PAID, LocalDateTime.now().plusMinutes(60), LocalDateTime.now().plusMinutes(90), member, new PaymentCardInfo("type", "issuerCorporation", "bin")));
        clear();

        // when
        Page<PaymentDto> paymentDtos = paymentRepository.findAllDtos(PageableBuilder.build());

        // then
        assertThat(paymentDtos.getTotalElements()).isEqualTo(2);
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
