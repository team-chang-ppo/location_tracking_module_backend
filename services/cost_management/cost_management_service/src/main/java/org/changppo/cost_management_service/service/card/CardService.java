package org.changppo.cost_management_service.service.card;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.cost_management_service.dto.card.CardCreateRequest;
import org.changppo.cost_management_service.dto.card.CardDto;
import org.changppo.cost_management_service.dto.card.CardListDto;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.card.PaymentGateway;
import org.changppo.cost_management_service.entity.card.PaymentGatewayType;
import org.changppo.cost_management_service.entity.member.Member;
import org.changppo.cost_management_service.response.exception.card.CardNotFoundException;
import org.changppo.cost_management_service.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.cost_management_service.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.card.PaymentGatewayRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.service.card.paymentgateway.PaymentGatewayClient;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@RequiredArgsConstructor
@Service
@Validated
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final MemberRepository memberRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;


    @Transactional
    public CardDto create(@Valid CardCreateRequest req) {
        PaymentGateway paymentGateway = paymentGatewayRepository.findByPaymentGatewayType(req.getPaymentGatewayType()).orElseThrow(PaymentGatewayNotFoundException::new);
        Member member = memberRepository.findById(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
        Card card = Card.builder()
                .key(req.getKey())
                .type(req.getType())
                .acquirerCorporation(req.getAcquirerCorporation())
                .issuerCorporation(req.getIssuerCorporation())
                .bin(req.getBin())
                .paymentGateway(paymentGateway)
                .member(member)
                .build();
        card = cardRepository.save(card);
        return new CardDto(card.getId(), card.getType(), card.getIssuerCorporation(), card.getBin(),
                card.getPaymentGateway().getPaymentGatewayType(), card.getCreatedAt());
    }

    @PreAuthorize("@cardAccessEvaluator.check(#id)")
    public CardDto read(@Param("id")Long id) {  // TODO. 트랜잭션 확인
        Card card = cardRepository.findByIdWithPaymentGateway(id).orElseThrow(CardNotFoundException::new);
        return new CardDto(card.getId(), card.getType(), card.getIssuerCorporation(), card.getBin(),
                card.getPaymentGateway().getPaymentGatewayType(), card.getCreatedAt());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public CardListDto readAll(@Param("memberId")Long memberId){
        List<CardDto> cards = cardRepository.findAllByMemberId(memberId);
        return new CardListDto(cards);
    }

    @Transactional
    @PreAuthorize("@cardAccessEvaluator.check(#id)")
    public void delete(@Param("id")Long id) {
        Card card = cardRepository.findByIdWithPaymentGateway(id).orElseThrow(CardNotFoundException::new);
        PaymentGatewayType paymentGatewayType = card.getPaymentGateway().getPaymentGatewayType();
        paymentGatewayClients.stream()
                .filter(client -> client.supports(paymentGatewayType))
                .findFirst()
                .orElseThrow(UnsupportedPaymentGatewayException::new)
                .inactive(card.getKey());
        cardRepository.delete(card);
    }
}
