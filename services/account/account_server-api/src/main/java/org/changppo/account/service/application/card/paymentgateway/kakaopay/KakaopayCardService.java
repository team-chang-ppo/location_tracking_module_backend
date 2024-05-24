package org.changppo.account.service.application.card.paymentgateway.kakaopay;

import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.card.CardCreateRequest;
import org.changppo.account.dto.card.kakaopay.*;
import org.changppo.account.paymentgateway.kakaopay.dto.payment.KakaopayApproveResponse;
import org.changppo.account.response.exception.card.CardCreateFailureException;
import org.changppo.account.response.exception.paymentgateway.KakaopayPaymentGatewayFailException;
import org.changppo.account.service.application.card.CardService;
import org.changppo.account.service.domain.card.paymentgateway.KakaopayCardDomainService;
import org.changppo.account.service.dto.card.CardDto;
import org.changppo.account.type.PaymentGatewayType;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.changppo.account.paymentgateway.kakaopay.KakaopayConstants.*;

@RequiredArgsConstructor
@Service
public class KakaopayCardService {

    private final KakaopayCardDomainService kakaopayCardDomainService;
    private final CardService cardService;  // Card와 동일한 목적을 가졌으므로 사용 가능

    public KakaopayCardRegisterRedirectResponse registerReady(KakaopayCardRegisterReadyRequest req) {
        return kakaopayCardDomainService.registerReady(req.getMemberId());
    }

    public CardDto registerApprove(KakaopayCardRegisterApproveRequest req) {
        KakaopayApproveResponse kakaopayApproveResponse = kakaopayCardDomainService.registerApprove(req.getPartner_order_id(), req.getMemberId(), req.getPg_token());
        try {
            CardCreateRequest cardCreateRequest = createCardCreateRequest(kakaopayApproveResponse);
            return cardService.create(cardCreateRequest);
        } catch (Exception e) {  // 카카오페이 카드 등록 API는 성공 했으나 저장을 실패
            kakaopayCardDomainService.deactivateCard(kakaopayApproveResponse.getSid());  // 카카오페이 카드 연결 해제
            throw new CardCreateFailureException(e);
        }
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
        kakaopayCardDomainService.registerCancel(req.getPartner_order_id(), req.getMemberId());
    }

    public void registerFail(KakaopayCardRegisterFailRequest req) {
        kakaopayCardDomainService.registerFail(req.getPartner_order_id(), req.getMemberId());
        throw new KakaopayPaymentGatewayFailException();
    }
}
