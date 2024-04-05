package org.changppo.cost_management_service.service.card.paymentgateway.kakaopay;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.card.CardCreateRequest;
import org.changppo.cost_management_service.dto.card.CardDto;
import org.changppo.cost_management_service.dto.card.kakaopay.*;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.*;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.response.exception.card.CardCreateFailureException;
import org.changppo.cost_management_service.service.card.CardService;
import org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayPaymentGatewayClient;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayConstants.*;

@RequiredArgsConstructor
@Service
public class KakaopayCardService {

    private final KakaopayPaymentGatewayClient kakaopayPaymentGatewayClient;
    private final CardService cardService;

    public KakaopayCardRegisterRedirectResponse registerReady(KakaopayCardRegisterReadyRequest req) {
        KakaopayReadyResponse kakaopayReadyResponse = kakaopayPaymentGatewayClient.Ready(createKakaopayReadyRequest(req));
        return new KakaopayCardRegisterRedirectResponse(kakaopayReadyResponse.getNext_redirect_app_url(), kakaopayReadyResponse.getNext_redirect_mobile_url(),
                kakaopayReadyResponse.getNext_redirect_pc_url(), kakaopayReadyResponse.getAndroid_app_scheme(), kakaopayReadyResponse.getIos_app_scheme());
    }

    private KakaopayReadyRequest createKakaopayReadyRequest(KakaopayCardRegisterReadyRequest req) { // TODO. 실행 환경 별로 redirect url 변경
        String partnerOrderId = generateTemporaryValue();
        return new KakaopayReadyRequest(
                CCID,
                partnerOrderId,
                req.getMemberId(),
                MODULE_NAME,
                0,
                0,
                0,
                0,
                "http://localhost:8080" + APPROVE_CALLBACK_PATH + partnerOrderId,
                "http://localhost:8080" + CANCEL_CALLBACK_PATH + partnerOrderId,
                "http://localhost:8080" + FAIL_CALLBACK_PATH + partnerOrderId
        );
    }

    private String generateTemporaryValue() {
        return UUID.randomUUID().toString();
    }

    public CardDto registerApprove(KakaopayCardRegisterApproveRequest req) {
        KakaopayApproveResponse kakaopayApproveResponse = kakaopayPaymentGatewayClient.Approve(createKakaopayApproveRequest(req));
        try {
            CardCreateRequest cardCreateRequest = createCardCreateRequest(kakaopayApproveResponse);
            return cardService.create(cardCreateRequest);
        } catch (Exception e) {
            kakaopayPaymentGatewayClient.inactive(kakaopayApproveResponse.getSid());
            throw new CardCreateFailureException(e);
        }
    }

    private KakaopayApproveRequest createKakaopayApproveRequest(KakaopayCardRegisterApproveRequest req) {
        return new KakaopayApproveRequest(
                CCID,
                req.getPartner_order_id(),
                req.getMemberId(),
                req.getPg_token()
        );
    }

    private CardCreateRequest createCardCreateRequest(KakaopayApproveResponse response) {
        KakaopayCardDto kakaopayCardDto = createKakaopayCardDto(response);
        Long memberId = Optional.ofNullable(response.getPartner_user_id())
                .map(Long::valueOf)
                .orElseThrow(() -> new IllegalArgumentException("invalid partner_user_id"));
        return new CardCreateRequest(response.getSid(), kakaopayCardDto.getType(), kakaopayCardDto.getAcquirerCorporation(),
                kakaopayCardDto.getIssuerCorporation(), kakaopayCardDto.getBin(), PaymentGatewayType.PG_KAKAOPAY, memberId);
    }

    private KakaopayCardDto createKakaopayCardDto(KakaopayApproveResponse response) {
        if (CARD.equalsIgnoreCase(response.getPayment_method_type())) {
            return new KakaopayCardDto(
                    response.getCard_info().getCard_type(),
                    response.getCard_info().getKakaopay_purchase_corp(),
                    response.getCard_info().getKakaopay_issuer_corp(),
                    response.getCard_info().getBin()
            );
        } else if (MONEY.equalsIgnoreCase(response.getPayment_method_type())) {
            return new KakaopayCardDto(MONEY_TYPE, MONEY_CORPORATION, MONEY_CORPORATION, MONEY_BIN);
        }
        throw new IllegalArgumentException("Unsupported payment method type: " + response.getPayment_method_type());
    }

    public void registerCancel(KakaopayCardRegisterCancelRequest req) {
        kakaopayPaymentGatewayClient.cancel(req.getPartner_order_id());
    }

    public void registerFail(KakaopayCardRegisterFailRequest req) {
        kakaopayPaymentGatewayClient.fail(createKakaopayCancelRequest(req));
    }

    private KakaopayCancelRequest createKakaopayCancelRequest(KakaopayCardRegisterFailRequest req) {
        return new KakaopayCancelRequest(
                req.getPartner_order_id(),
                req.getMemberId()
        );
    }
}