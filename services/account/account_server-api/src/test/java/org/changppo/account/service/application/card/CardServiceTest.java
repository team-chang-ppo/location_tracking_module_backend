package org.changppo.account.service.application.card;

import org.changppo.account.dto.card.CardCreateRequest;
import org.changppo.account.dto.card.CardListDto;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.card.CardDomainService;
import org.changppo.account.service.domain.card.PaymentGatewayDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.member.RoleDomainService;
import org.changppo.account.service.domain.member.SessionDomainService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.changppo.account.builder.card.CardBuilder.buildCard;
import static org.changppo.account.builder.card.CardCreateRequestBuilder.buildCardCreateRequest;
import static org.changppo.account.builder.card.CardDtoBuilder.buildCardDto;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.changppo.account.builder.pageable.PageableBuilder.buildPage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CardServiceTest {

    @InjectMocks
    private CardService cardService;
    @Mock
    private CardDomainService cardDomainService;
    @Mock
    private PaymentGatewayDomainService paymentGatewayDomainService;
    @Mock
    private MemberDomainService memberDomainService;
    @Mock
    private RoleDomainService roleDomainService;
    @Mock
    private ApiKeyDomainService apiKeyDomainService;
    @Mock
    private SessionDomainService sessionDomainService;

    Role freeRole, normalRole;
    Member freeMember, normalMember;
    PaymentGateway paymentGateway;

    @BeforeEach
    void beforeEach() {
        freeRole = buildRole(RoleType.ROLE_FREE);
        normalRole = buildRole(RoleType.ROLE_NORMAL);
        freeMember = buildMember(freeRole);
        normalMember = buildMember(normalRole);
        paymentGateway = buildPaymentGateway(PaymentGatewayType.PG_KAKAOPAY);
    }

    @Test
    void createTest() {
        // given
        Card card = buildCard(freeMember, paymentGateway);
        CardCreateRequest cardCreateRequest = buildCardCreateRequest(1L, paymentGateway.getPaymentGatewayType());
        given(memberDomainService.getMemberWithRoles(anyLong())).willReturn(freeMember);
        given(paymentGatewayDomainService.getPaymentGatewayByType(any(PaymentGatewayType.class))).willReturn(paymentGateway);
        given(cardDomainService.createCard(cardCreateRequest.getKey(), cardCreateRequest.getType(), cardCreateRequest.getAcquirerCorporation(), cardCreateRequest.getIssuerCorporation(), cardCreateRequest.getBin(), paymentGateway, freeMember)).willReturn(card);
        given(roleDomainService.hasFreeRole(freeMember.getRole().getRoleType())).willReturn(true);

        // when
        CardDto result = cardService.create(cardCreateRequest);

        // then
        assertThat(result.getType()).isEqualTo(card.getType());
        verify(roleDomainService).upgradeRole(freeMember);
        verify(apiKeyDomainService).unbanForCardDeletion(freeMember.getId());
        verify(sessionDomainService).updateAuthentication(freeMember);
    }

    @Test
    void readTest() {
        // given
        Card card = buildCard(freeMember, paymentGateway);
        given(cardDomainService.getCard(anyLong())).willReturn(card);

        // when
        CardDto result = cardService.read(1L);

        // then
        assertThat(result.getBin()).isEqualTo(card.getBin());
        assertThat(result.getIssuerCorporation()).isEqualTo(card.getIssuerCorporation());
        assertThat(result.getType()).isEqualTo(card.getType());
        assertThat(result.getPaymentGateway()).isEqualTo(card.getPaymentGateway().getPaymentGatewayType());
    }

    @Test
    void readAllTest() {
        // given
        List<CardDto> cardDtos = List.of(buildCardDto(paymentGateway), buildCardDto(paymentGateway));
        given(cardDomainService.getCardDtos(anyLong())).willReturn(cardDtos);

        // when
        CardListDto result = cardService.readAll(1L);

        // then
        assertThat(result.getCardList()).isEqualTo(cardDtos);
    }

    @Test
    void readListTest() {
        // given
        Pageable pageable = buildPage();
        CardDto cardDto = buildCardDto(paymentGateway);
        List<CardDto> cardDtos = List.of(cardDto, cardDto);
        Page<CardDto> page = new PageImpl<>(cardDtos, pageable, cardDtos.size());
        given(cardDomainService.getCardDtos(any(Pageable.class))).willReturn(page);

        // when
        Page<CardDto> result = cardService.readList(pageable);

        // then
        assertThat(result.getContent()).isEqualTo(cardDtos);
    }

    @Test
    void deleteTest() {
        // given
        Card card = buildCard(normalMember, paymentGateway);
        given(cardDomainService.getCard(anyLong())).willReturn(card);
        doNothing().when(cardDomainService).deleteCard(anyLong());
        doNothing().when(cardDomainService).inactivateCard(anyString(), any(PaymentGatewayType.class));
        given(cardDomainService.isLastCard(normalMember.getId())).willReturn(true);

        // when
        cardService.delete(1L);

        // then
        verify(cardDomainService).deleteCard(1L);
        verify(cardDomainService).inactivateCard(card.getKey(), card.getPaymentGateway().getPaymentGatewayType());
        verify(roleDomainService).downgradeRole(normalMember);
        verify(apiKeyDomainService).banForCardDeletion(normalMember.getId());
        verify(sessionDomainService).updateAuthentication(normalMember);
    }
}
