package org.changppo.cost_management_service.service.card.paymentgateway.kakaopay;

import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.card.CardCreateRequest;
import org.changppo.cost_management_service.dto.card.CardDto;
import org.changppo.cost_management_service.dto.card.kakaopay.*;
import org.changppo.cost_management_service.dto.paymentgateway.kakaopay.payment.*;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.exception.card.CardCreateFailureException;
import org.changppo.cost_management_service.service.card.CardService;
import org.changppo.cost_management_service.service.paymentgateway.kakaopay.KakaopayPaymentGatewayClient;
import org.springframework.stereotype.Service;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaopayCardService {

    private final KakaopayPaymentGatewayClient kakaopayPaymentGatewayClient;
    private final CardService cardService;
    private static final String CARD = "CARD";
    private static final String MONEY = "MONEY";
    private static final String MONEY_TYPE = "현금";
    private static final String MONEY_CORPORATION = "카카오페이";
    private static final String MONEY_BIN = "머니";
    private static final String CCID = "CCID";

    public KakaopayCardRegisterRedirectResponse registerReady(KakaopayCardRegisterReadyRequest req) {
        KakaopayReadyResponse kakaopayReadyResponse = kakaopayPaymentGatewayClient.Ready(createKakaopayReadyRequest(req));
        return new KakaopayCardRegisterRedirectResponse(kakaopayReadyResponse.getNext_redirect_app_url(), kakaopayReadyResponse.getNext_redirect_mobile_url(),
                kakaopayReadyResponse.getNext_redirect_pc_url(), kakaopayReadyResponse.getAndroid_app_scheme(), kakaopayReadyResponse.getIos_app_scheme());
    }

    private KakaopayReadyRequest createKakaopayReadyRequest(KakaopayCardRegisterReadyRequest req) {
        String partnerOrderId = generateTemporaryValue();
        return new KakaopayReadyRequest(
                "CCID",
                partnerOrderId,
                req.getMemberId(),
                "위치추적모듈 정기결제",
                0,
                0,
                0,
                0,
                "http://localhost:8080/api/cards/v1/kakaopay/register/approve?partner_order_id=" + partnerOrderId,
                "http://localhost:8080/api/cards/v1/kakaopay/register/cancel?partner_order_id=" + partnerOrderId,
                "http://localhost:8080/api/cards/v1/kakaopay/register/fail?partner_order_id=" + partnerOrderId
        );
    }

    private String generateTemporaryValue() {
        return UUID.randomUUID().toString();
    }

    public CardDto registerApprove(KakaopayCardRegisterApproveRequest req) {
        KakaopayApproveResponse kakaopayApproveResponse = kakaopayPaymentGatewayClient.Approve(createKakaopayApproveRequest(req));
        try {
            CardCreateRequest cardCreateRequest = createCardCreateRequest(kakaopayApproveResponse, req.getMemberId());
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

    private CardCreateRequest createCardCreateRequest(KakaopayApproveResponse response, Long memberId) {
        KakaopayCardDto kakaopayCardDto = createKakaopayCardDto(response);
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