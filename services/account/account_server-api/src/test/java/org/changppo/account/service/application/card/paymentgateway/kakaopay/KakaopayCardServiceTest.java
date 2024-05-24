package org.changppo.account.service.application.card.paymentgateway.kakaopay;

import org.changppo.account.dto.card.CardCreateRequest;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.entity.member.Role;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.response.exception.card.CardCreateFailureException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayFailException;
import org.changppo.account.service.application.card.CardService;
import org.changppo.account.service.domain.card.paymentgateway.KakaopayCardDomainService;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.account.type.PaymentGatewayType;
import org.changppo.account.type.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.changppo.account.builder.card.CardDtoBuilder.buildCardDto;
import static org.changppo.account.builder.card.PaymentGatewayBuilder.buildPaymentGateway;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayRequestBuilder.*;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayApproveResponse;
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder.buildKakaopayCardRegisterRedirectResponse;
import static org.changppo.account.builder.member.MemberBuilder.buildMember;
import static org.changppo.account.builder.member.RoleBuilder.buildRole;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KakaopayCardServiceTest {

    @InjectMocks
    private KakaopayCardService kakaopayCardService;
    @Mock
    private KakaopayCardDomainService kakaopayCardDomainService;
    @Mock
    private CardService cardService;
    @Captor
    private ArgumentCaptor<CardCreateRequest> cardCreateRequestCaptor;

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
    void registerReadyTest() {
        // given
        KakaopayCardRegisterReadyRequest request = buildKakaopayCardRegisterReadyRequest(1L);
        KakaopayCardRegisterRedirectResponse response = buildKakaopayCardRegisterRedirectResponse();
        given(kakaopayCardDomainService.registerReady(anyLong())).willReturn(response);

        // when
        KakaopayCardRegisterRedirectResponse result = kakaopayCardService.registerReady(request);

        // then
        assertThat(result).isEqualTo(response);
        verify(kakaopayCardDomainService).registerReady(request.getMemberId());
    }

    @Test
    void registerApproveTest() {
        // given
        CardDto cardDto = buildCardDto(paymentGateway);
        KakaopayCardRegisterApproveRequest request = buildKakaopayCardRegisterApproveRequest(1L);
        KakaopayApproveResponse response = buildKakaopayApproveResponse(1L, LocalDateTime.now());
        given(kakaopayCardDomainService.registerApprove(anyString(), anyLong(), anyString())).willReturn(response);
        given(cardService.create(any(CardCreateRequest.class))).willReturn(cardDto);

        // when
        CardDto result = kakaopayCardService.registerApprove(request);

        // then
        assertThat(result).isEqualTo(cardDto);
        verify(cardService).create(cardCreateRequestCaptor.capture());
        CardCreateRequest capturedRequest = cardCreateRequestCaptor.getValue();
        assertThat(capturedRequest.getKey()).isEqualTo(response.getSid());
        assertThat(capturedRequest.getType()).isEqualTo(response.getCardType());
        assertThat(capturedRequest.getIssuerCorporation()).isEqualTo(response.getCardIssuerCorporation());
        assertThat(capturedRequest.getBin()).isEqualTo(response.getCardBin());
        assertThat(capturedRequest.getPaymentGatewayType()).isEqualTo(PaymentGatewayType.PG_KAKAOPAY);
    }

    @Test
    void registerApproveFailureTest() {
        // given
        KakaopayCardRegisterApproveRequest request = buildKakaopayCardRegisterApproveRequest(1L);
        KakaopayApproveResponse response = buildKakaopayApproveResponse(1L, LocalDateTime.now());
        given(kakaopayCardDomainService.registerApprove(anyString(), anyLong(), anyString())).willReturn(response);
        doThrow(new RuntimeException("DB Save Error")).when(cardService).create(any(CardCreateRequest.class));

        // when, then
        assertThatThrownBy(() -> kakaopayCardService.registerApprove(request)).isInstanceOf(CardCreateFailureException.class);

        verify(kakaopayCardDomainService).deactivateCard(response.getSid());
    }

    @Test
    void registerCancelTest() {
        // given
        KakaopayCardRegisterCancelRequest request = buildKakaopayCardRegisterCancelRequest(1L);
        doNothing().when(kakaopayCardDomainService).registerCancel(anyString(), anyLong());

        // when
        kakaopayCardService.registerCancel(request);

        // then
        verify(kakaopayCardDomainService).registerCancel(request.getPartner_order_id(), request.getMemberId());
    }

    @Test
    void registerFailTest() {
        // given
        KakaopayCardRegisterFailRequest request = buildKakaopayCardRegisterFailRequest(1L);
        doNothing().when(kakaopayCardDomainService).registerFail(anyString(), anyLong());

        // when, then
        assertThatThrownBy(() -> kakaopayCardService.registerFail(request)).isInstanceOf(KakaopayPaymentGatewayFailException.class);

        verify(kakaopayCardDomainService).registerFail(request.getPartner_order_id(), request.getMemberId());
    }
}
