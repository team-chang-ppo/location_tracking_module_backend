package org.changppo.account.service.application.card;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.changppo.account.dto.card.CardCreateRequest;
import org.changppo.account.dto.card.CardListDto;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.service.domain.apikey.ApiKeyDomainService;
import org.changppo.account.service.domain.card.CardDomainService;
import org.changppo.account.service.domain.card.PaymentGatewayDomainService;
import org.changppo.account.service.domain.member.MemberDomainService;
import org.changppo.account.service.domain.member.RoleDomainService;
import org.changppo.account.service.domain.member.SessionDomainService;
import org.changppo.account.service.dto.card.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@RequiredArgsConstructor
@Service
@Validated
@Transactional(readOnly = true)
public class CardService {

    private final CardDomainService cardDomainService;
    private final PaymentGatewayDomainService paymentGatewayDomainService;
    private final MemberDomainService memberDomainService;
    private final RoleDomainService roleDomainService;
    private final ApiKeyDomainService apiKeyDomainService;
    private final SessionDomainService sessionDomainService;

    @Transactional
    public CardDto create(@Valid CardCreateRequest req) { // PaymentGateway에서 호출 되므로 @Validated 사용
        Member member = memberDomainService.getMemberWithRoles(req.getMemberId());
        PaymentGateway paymentGateway = paymentGatewayDomainService.getPaymentGatewayByType(req.getPaymentGatewayType());
        Card card = cardDomainService.createCard(req.getKey(), req.getType(), req.getAcquirerCorporation(), 
                                                    req.getIssuerCorporation(), req.getBin(), paymentGateway, member);

        if (roleDomainService.hasFreeRole(member.getRole().getRoleType())) {
            roleDomainService.upgradeRole(member);
            apiKeyDomainService.unbanForCardDeletion(member.getId());
            sessionDomainService.updateAuthentication(card.getMember());
        }

        return new CardDto(card.getId(), card.getType(), card.getIssuerCorporation(), card.getBin(),
                            card.getPaymentGateway().getPaymentGatewayType(), card.getCreatedAt());
    }
    
    @PreAuthorize("@cardAccessEvaluator.check(#id)")
    public CardDto read(@Param("id")Long id) {
        Card card = cardDomainService.getCard(id);  // 영속성 컨텍스트에서 들고와 불필요한 Query 최소화
        return new CardDto(card.getId(), card.getType(), card.getIssuerCorporation(), card.getBin(),
                card.getPaymentGateway().getPaymentGatewayType(), card.getCreatedAt());
    }

    @PreAuthorize("@memberAccessEvaluator.check(#memberId)")
    public CardListDto readAll(@Param("memberId")Long memberId){
        return new CardListDto(cardDomainService.getCardDtos(memberId));
    }

    public Page<CardDto> readList(Pageable pageable) {
        return cardDomainService.getCardDtos(pageable);
    }

    @Transactional
    @PreAuthorize("@cardAccessEvaluator.check(#id)")
    public void delete(@Param("id")Long id) {
        Card card = cardDomainService.getCard(id);  // 영속성 컨텍스트에서 들고와 불필요한 Query 최소화
        cardDomainService.deleteCard(id);
        cardDomainService.inactivateCard(card.getKey(), card.getPaymentGateway().getPaymentGatewayType());

        if (cardDomainService.isLastCard(card.getMember().getId())) {
            roleDomainService.downgradeRole(card.getMember());
            apiKeyDomainService.banForCardDeletion(card.getMember().getId());
            sessionDomainService.updateAuthentication(card.getMember());
        }
    }
}
