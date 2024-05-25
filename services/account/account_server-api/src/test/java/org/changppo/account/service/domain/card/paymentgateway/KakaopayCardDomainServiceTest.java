package org.changppo.account.service.domain.card.paymentgateway;

import org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayResponseBuilder;
import org.changppo.account.config.ApiServerUrlProperties;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.paymentgateway.kakaopay.KakaopayPaymentGatewayClient;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.*;
import org.changppo.account.response.ClientResponse;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayApproveFailureException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayReadyFailureException;
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
import static org.changppo.account.builder.card.paymentgateway.kakaopay.KakaopayRequestBuilder.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class KakaopayCardDomainServiceTest {

    @InjectMocks
    private KakaopayCardDomainService kakaopayCardDomainService;
    @Mock
    private KakaopayPaymentGatewayClient kakaopayPaymentGatewayClient;
    @Mock
    private ApiServerUrlProperties apiServerUrlProperties;
    @Captor
    private ArgumentCaptor<KakaopayReadyRequest> readyRequestCaptor;
    @Captor
    private ArgumentCaptor<KakaopayApproveRequest> approveRequestCaptor;
    @Captor
    private ArgumentCaptor<KakaopayCancelRequest> cancelRequestCaptor;
    @Captor
    private ArgumentCaptor<KakaopayFailRequest> failRequestCaptor;

    @Test
    void registerReadyTest() {
        // given
        KakaopayCardRegisterReadyRequest request = buildKakaopayCardRegisterReadyRequest(1L);
        KakaopayReadyResponse response = KakaopayResponseBuilder.buildKakaopayReadyResponse(LocalDateTime.now());
        given(apiServerUrlProperties.getUrl()).willReturn("http://testhost");
        given(kakaopayPaymentGatewayClient.Ready(any())).willReturn(ClientResponse.success(response));

        // when
        KakaopayCardRegisterRedirectResponse result = kakaopayCardDomainService.registerReady(request.getMemberId());

        // then
        assertThat(result.getNextRedirectAppUrl()).isEqualTo(response.getNext_redirect_app_url());
        assertThat(result.getNextRedirectMobileUrl()).isEqualTo(response.getNext_redirect_mobile_url());
        assertThat(result.getNextRedirectPcUrl()).isEqualTo(response.getNext_redirect_pc_url());
        verify(kakaopayPaymentGatewayClient).Ready(readyRequestCaptor.capture());
        KakaopayReadyRequest capturedRequest = readyRequestCaptor.getValue();
        assertThat(capturedRequest.getPartnerUserId()).isEqualTo(request.getMemberId());
    }

    @Test
    void registerReadyFailureTest() {
        // given
        KakaopayCardRegisterReadyRequest kakaopayCardRegisterReadyRequest = buildKakaopayCardRegisterReadyRequest(1L);
        given(apiServerUrlProperties.getUrl()).willReturn("http://testhost");
        given(kakaopayPaymentGatewayClient.Ready(any())).willReturn(ClientResponse.failure());

        // when, then
        assertThatThrownBy(() -> kakaopayCardDomainService.registerReady(kakaopayCardRegisterReadyRequest.getMemberId()))
                .isInstanceOf(KakaopayPaymentGatewayReadyFailureException.class);
    }

    @Test
    void registerApproveTest() {
        // given
        KakaopayCardRegisterApproveRequest request = buildKakaopayCardRegisterApproveRequest();
        KakaopayApproveResponse response = KakaopayResponseBuilder.buildKakaopayApproveResponse(1L, LocalDateTime.now());
        given(kakaopayPaymentGatewayClient.Approve(any())).willReturn(ClientResponse.success(response));

        // when
        KakaopayApproveResponse result = kakaopayCardDomainService.registerApprove(request.getPartner_order_id(), request.getMemberId(), request.getPg_token());

        // then
        assertThat(result).isEqualTo(response);
        verify(kakaopayPaymentGatewayClient).Approve(approveRequestCaptor.capture());
        KakaopayApproveRequest capturedRequest = approveRequestCaptor.getValue();
        assertThat(capturedRequest.getPartnerOrderId()).isEqualTo(request.getPartner_order_id());
        assertThat(capturedRequest.getPartnerUserId()).isEqualTo(request.getMemberId());
        assertThat(capturedRequest.getPgToken()).isEqualTo(request.getPg_token());
    }

    @Test
    void registerApproveFailureTest() {
        // given
        KakaopayCardRegisterApproveRequest request = buildKakaopayCardRegisterApproveRequest();
        given(kakaopayPaymentGatewayClient.Approve(any())).willReturn(ClientResponse.failure());

        // when, then
        assertThatThrownBy(() -> kakaopayCardDomainService.registerApprove(request.getPartner_order_id(), request.getMemberId(), request.getPg_token()))
                .isInstanceOf(KakaopayPaymentGatewayApproveFailureException.class);
    }

    @Test
    void deactivateCardTest() {
        // given
        String sid = "sid";

        // when
        kakaopayCardDomainService.deactivateCard(sid);

        // then
        verify(kakaopayPaymentGatewayClient).inactive(sid);
    }

    @Test
    void registerCancelTest() {
        // given
        KakaopayCardRegisterCancelRequest request = buildKakaopayCardRegisterCancelRequest();

        // when
        kakaopayCardDomainService.registerCancel(request.getPartner_order_id(), request.getMemberId());

        // then
        verify(kakaopayPaymentGatewayClient).cancel(cancelRequestCaptor.capture());
        KakaopayCancelRequest capturedRequest = cancelRequestCaptor.getValue();
        assertThat(capturedRequest.getPartnerOrderId()).isEqualTo(request.getPartner_order_id());
        assertThat(capturedRequest.getPartnerUserId()).isEqualTo(request.getMemberId());
    }

    @Test
    void registerFailTest() {
        // given
        KakaopayCardRegisterFailRequest request = buildKakaopayCardRegisterFailRequest();

        // when
        kakaopayCardDomainService.registerFail(request.getPartner_order_id(), request.getMemberId());

        // then
        verify(kakaopayPaymentGatewayClient).fail(failRequestCaptor.capture());
        KakaopayFailRequest capturedRequest = failRequestCaptor.getValue();
        assertThat(capturedRequest.getPartnerOrderId()).isEqualTo(request.getPartner_order_id());
        assertThat(capturedRequest.getPartnerUserId()).isEqualTo(request.getMemberId());
    }
}
