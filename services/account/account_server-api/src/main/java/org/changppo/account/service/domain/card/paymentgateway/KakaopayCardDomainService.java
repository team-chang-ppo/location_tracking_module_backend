package org.changppo.account.service.domain.card.paymentgateway;

import lombok.RequiredArgsConstructor;
import org.changppo.account.config.ApiServerUrlProperties;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.paymentgateway.kakaopay.KakaopayPaymentGatewayClient;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.*;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayApproveFailureException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayReadyFailureException;
import org.springframework.stereotype.Service;
import java.util.UUID;

import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.*;

@RequiredArgsConstructor
@Service
public class KakaopayCardDomainService {

    private final KakaopayPaymentGatewayClient kakaopayPaymentGatewayClient;
    private final ApiServerUrlProperties apiServerUrlProperties;

    public KakaopayCardRegisterRedirectResponse registerReady(Long memberId) {
        KakaopayReadyResponse response = kakaopayPaymentGatewayClient.Ready(createKakaopayReadyRequest(memberId)).getData().orElseThrow(KakaopayPaymentGatewayReadyFailureException::new);
        return new KakaopayCardRegisterRedirectResponse(response.getNext_redirect_app_url(), response.getNext_redirect_mobile_url(),
                response.getNext_redirect_pc_url(), response.getAndroid_app_scheme(), response.getIos_app_scheme());
    }

    private KakaopayReadyRequest createKakaopayReadyRequest(Long memberId) {
        String partnerOrderId = generateTemporaryValue();
        return new KakaopayReadyRequest(
                CCID,
                partnerOrderId,
                memberId,
                MODULE_NAME,
                0,
                0,
                0,
                apiServerUrlProperties.getUrl() + APPROVE_CALLBACK_PATH + partnerOrderId,
                apiServerUrlProperties.getUrl() + CANCEL_CALLBACK_PATH + partnerOrderId,
                apiServerUrlProperties.getUrl() + FAIL_CALLBACK_PATH + partnerOrderId
        );
    }

    private String generateTemporaryValue() {
        return UUID.randomUUID().toString();
    }

    public KakaopayApproveResponse registerApprove(String partnerOrderId, Long memberId, String pgToken) {
        return kakaopayPaymentGatewayClient.Approve(createKakaopayApproveRequest(partnerOrderId, memberId, pgToken)).getData().orElseThrow(KakaopayPaymentGatewayApproveFailureException::new);
    }

    private KakaopayApproveRequest createKakaopayApproveRequest(String partnerOrderId, Long memberId, String pgToken) {
        return new KakaopayApproveRequest(
                CCID,
                partnerOrderId,
                memberId,
                pgToken
        );
    }

    public void deactivateCard(String sid) {
        kakaopayPaymentGatewayClient.inactive(sid);
    }

    public void registerCancel(String partnerOrderId, Long memberId) {
        kakaopayPaymentGatewayClient.cancel(createKakaopayCancelRequest(partnerOrderId, memberId));
    }

    private KakaopayCancelRequest createKakaopayCancelRequest(String partnerOrderId, Long memberId) {
        return new KakaopayCancelRequest(
                partnerOrderId,
                memberId
        );
    }

    public void registerFail(String partnerOrderId, Long memberId) {
        kakaopayPaymentGatewayClient.fail(createKakaopayFailRequest(partnerOrderId, memberId));
    }

    private KakaopayFailRequest createKakaopayFailRequest(String partnerOrderId, Long memberId) {
        return new KakaopayFailRequest(
                partnerOrderId,
                memberId
        );
    }
}
