package org.changppo.account.service.domain.card;

import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.card.CardBuilder.buildCard;
import static org.changppo.account.builder.card.CardDtoBuilder.buildCardDto;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CardDomainServiceTest {

    @InjectMocks
    private CardDomainService cardDomainService;
    @Mock
    private CardRepository cardRepository;

    Member member;
    Role role;
    PaymentGateway paymentGateway;

    @BeforeEach
    void beforeEach() {
        role = buildRole(RoleType.ROLE_NORMAL);
        member = buildMember(role);
        paymentGateway = buildPaymentGateway(PaymentGatewayType.PG_KAKAOPAY);
    }

    @Test
    void createCardTest() {
        // given
        Card card = buildCard(member, paymentGateway);
        given(cardRepository.save(any(Card.class))).willReturn(card);

        // when
        Card createdCard = cardDomainService.createCard(card.getKey(), card.getType(), card.getAcquirerCorporation(), card.getIssuerCorporation(), card.getBin(), card.getPaymentGateway(), member);

        // then
        assertThat(createdCard).isEqualTo(card);
    }

    @Test
    void getCardTest() {
        // given
        Card card = buildCard(member, paymentGateway);
        given(cardRepository.findById(anyLong())).willReturn(Optional.of(card));

        // when
        Card result = cardDomainService.getCard(1L);

        // then
        assertThat(result).isEqualTo(card);
    }

    @Test
    void getCardExceptionTest() {
        // given
        given(cardRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> cardDomainService.getCard(1L)).isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void getCardDtosTest() {
        // given
        List<CardDto> cardDtos = List.of(buildCardDto(paymentGateway), buildCardDto(paymentGateway));
        given(cardRepository.findAllDtosByMemberIdOrderByAsc(anyLong())).willReturn(cardDtos);

        // when
        List<CardDto> result = cardDomainService.getCardDtos(1L);

        // then
        assertThat(result).isEqualTo(cardDtos);
    }

    @Test
    void isLastCardTest() {
        // given
        given(cardRepository.countByMemberId(anyLong())).willReturn(0L);

        // when
        boolean result = cardDomainService.isLastCard(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void deleteCardTest() {
        // given
        Card card = buildCard(member, paymentGateway);
        given(cardRepository.findById(anyLong())).willReturn(Optional.of(card));
        doNothing().when(cardRepository).delete(any(Card.class));

        // when
        cardDomainService.deleteCard(1L);

        // then
        verify(cardRepository).delete(card);
    }

    @Test
    void deleteCardExceptionTest() {
        // given
        given(cardRepository.findById(anyLong())).willReturn(Optional.empty());

        // when, then
        assertThatThrownBy(() -> cardDomainService.deleteCard(1L)).isInstanceOf(CardNotFoundException.class);
    }

    @Test
    void getCardDtoPageTest() {
        // given
        Pageable pageable = buildPage();
        List<CardDto> cardDtos = List.of(buildCardDto(paymentGateway), buildCardDto(paymentGateway));
        Page<CardDto> cardDtoPage = new PageImpl<>(cardDtos, pageable, cardDtos.size());
        given(cardRepository.findAllDtos(any(Pageable.class))).willReturn(cardDtoPage);

        // when
        Page<CardDto> result = cardDomainService.getCardDtos(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(cardDtos);
    }
}
