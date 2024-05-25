package org.changppo.account.repository.card;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.changppo.account.builder.member.MemberBuilder;
import org.changppo.account.builder.pageable.PageableBuilder;
import org.changppo.account.config.QuerydslConfig;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.member.MemberRepository;
import org.changppo.account.repository.member.RoleRepository;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.changppo.account.builder.card.CardBuilder.buildCard;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.type.PaymentGatewayType.PG_KAKAOPAY;

@DataJpaTest
@Import(QuerydslConfig.class)
class CardRepositoryTest {

    @Autowired
    CardRepository cardRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PaymentGatewayRepository paymentGatewayRepository;
    @PersistenceContext
    EntityManager em;

    Role role;
    Member member;
    PaymentGateway paymentGateway;

    @BeforeEach
    void beforeEach() {
        role = roleRepository.save(buildRole(RoleType.ROLE_NORMAL));
        member = memberRepository.save(MemberBuilder.buildMember(role));
        paymentGateway = paymentGatewayRepository.save(buildPaymentGateway(PG_KAKAOPAY));
    }

    @Test
    void createTest() {
        // given
        Card card = buildCard(member, paymentGateway);

        // when
        cardRepository.save(card);
        clear();

        // then
        Card foundCard = cardRepository.findById(card.getId()).orElseThrow(CardNotFoundException::new);
        assertThat(foundCard.getKey()).isEqualTo(card.getKey());
        assertThat(foundCard.getType()).isEqualTo(card.getType());
        assertThat(foundCard.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    void uniqueKeyTest() {
        // given
        cardRepository.save(buildCard(member, paymentGateway));
        clear();

        // when, then
        assertThatThrownBy(() -> cardRepository.save(buildCard(member, paymentGateway)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dateTest() {
        // given
        Card card = buildCard(member, paymentGateway);

        // when
        cardRepository.save(card);
        clear();

        // then
        Card foundCard = cardRepository.findById(card.getId()).orElseThrow(CardNotFoundException::new);
        assertThat(foundCard.getCreatedAt()).isNotNull();
        assertThat(foundCard.getModifiedAt()).isNotNull();
        assertThat(foundCard.getCreatedAt()).isEqualTo(foundCard.getModifiedAt());
    }

    @Test
    void deleteTest() {
        // given
        Card card = buildCard(member, paymentGateway);
        cardRepository.save(card);
        clear();

        // when
        cardRepository.delete(card);
        clear();

        // then
        assertThatThrownBy(() -> cardRepository.findById(card.getId()).orElseThrow(CardNotFoundException::new))
                .isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void findAllCardByMemberIdOrderByAsc() {
        // given
        Card card1 = cardRepository.save(buildCard("testKey1", member, paymentGateway));
        Card card2 = cardRepository.save(buildCard("testKey2", member, paymentGateway));
        clear();

        // when
        List<Card> cards = cardRepository.findAllCardByMemberIdOrderByAsc(member.getId());

        // then
        assertThat(cards.size()).isEqualTo(2);
        assertThat(cards.get(0).getId()).isEqualTo(card1.getId());
        assertThat(cards.get(1).getId()).isEqualTo(card2.getId());
    }

    @Test
    void countByMemberId() {
        // given
        cardRepository.save(buildCard("testKey1", member, paymentGateway));
        cardRepository.save(buildCard("testKey2", member, paymentGateway));
        clear();

        // when
        long count = cardRepository.countByMemberId(member.getId());

        // then
        assertThat(count).isEqualTo(2);
    }

    @Test
    void deleteAllByMemberId() {
        // given
        cardRepository.save(buildCard("testKey1", member, paymentGateway));
        cardRepository.save(buildCard("testKey2", member, paymentGateway));
        clear();

        // when
        cardRepository.deleteAllByMemberId(member.getId());
        clear();

        // then
        List<Card> cards = cardRepository.findAll();
        assertThat(cards.isEmpty()).isTrue();
    }

    @Test
    void findAllDtosByMemberIdOrderByAsc() {
        // given
        Card card1 = cardRepository.save(buildCard("testKey1", member, paymentGateway));
        Card card2 = cardRepository.save(buildCard("testKey2", member, paymentGateway));
        clear();

        // when
        List<CardDto> cardDtos = cardRepository.findAllDtosByMemberIdOrderByAsc(member.getId());

        // then
        assertThat(cardDtos.size()).isEqualTo(2);
        assertThat(cardDtos.get(0).getId()).isEqualTo(card1.getId());
        assertThat(cardDtos.get(1).getId()).isEqualTo(card2.getId());
    }

    @Test
    void findAllDtos() {
        // given
        cardRepository.save(buildCard("testKey1", member, paymentGateway));
        cardRepository.save(buildCard("testKey2", member, paymentGateway));
        clear();

        // when
        Page<CardDto> cardDtos = cardRepository.findAllDtos(PageableBuilder.buildPage());

        // then
        assertThat(cardDtos.getTotalElements()).isEqualTo(2);
    }

    private void clear() {
        em.flush();
        em.clear();
    }
}
