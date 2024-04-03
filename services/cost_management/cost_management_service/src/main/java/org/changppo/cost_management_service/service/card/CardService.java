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
import org.changppo.cost_management_service.entity.member.Role;
import org.changppo.cost_management_service.entity.member.RoleType;
import org.changppo.cost_management_service.repository.apikey.ApiKeyRepository;
import org.changppo.cost_management_service.repository.card.CardRepository;
import org.changppo.cost_management_service.repository.card.PaymentGatewayRepository;
import org.changppo.cost_management_service.repository.member.MemberRepository;
import org.changppo.cost_management_service.repository.member.RoleRepository;
import org.changppo.cost_management_service.response.exception.card.CardNotFoundException;
import org.changppo.cost_management_service.response.exception.card.PaymentGatewayNotFoundException;
import org.changppo.cost_management_service.response.exception.card.UnsupportedPaymentGatewayException;
import org.changppo.cost_management_service.response.exception.member.MemberNotFoundException;
import org.changppo.cost_management_service.response.exception.member.RoleNotFoundException;
import org.changppo.cost_management_service.response.exception.member.UpdateAuthenticationFailureException;
import org.changppo.cost_management_service.service.card.paymentgateway.PaymentGatewayClient;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Validated
@Transactional(readOnly = true)
public class CardService {

    private final CardRepository cardRepository;
    private final PaymentGatewayRepository paymentGatewayRepository;
    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final ApiKeyRepository apiKeyRepository;
    private final List<PaymentGatewayClient> paymentGatewayClients;

    @Transactional
    public CardDto create(@Valid CardCreateRequest req) { // PaymentGateway에서 호출 되므로 @Validated 사용
        PaymentGateway paymentGateway = paymentGatewayRepository.findByPaymentGatewayType(req.getPaymentGatewayType()).orElseThrow(PaymentGatewayNotFoundException::new);
        Member member = memberRepository.findByIdWithRoles(req.getMemberId()).orElseThrow(MemberNotFoundException::new);
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
        upgradeRole(member);
        return new CardDto(card.getId(), card.getType(), card.getIssuerCorporation(), card.getBin(),
                card.getPaymentGateway().getPaymentGatewayType(), card.getCreatedAt());
    }

    private void upgradeRole(Member member) {
        boolean hasFreeRole = member.getMemberRoles().stream().anyMatch(memberRole -> memberRole.getRole().getRoleType() == RoleType.ROLE_FREE);
        if (hasFreeRole) {
            Role normalRole = roleRepository.findByRoleType(RoleType.ROLE_NORMAL).orElseThrow(RoleNotFoundException::new);
            member.changeRole(RoleType.ROLE_FREE, normalRole);
            updateAuthentication(member);
            unbanForCardDeletionApiKey(member);
        }
    }

    private void unbanForCardDeletionApiKey(Member member) {
        apiKeyRepository.unbanForCardDeletionByMemberId(member.getId());
    }

    @PreAuthorize("@cardAccessEvaluator.check(#id)")
    public CardDto read(@Param("id")Long id) {
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
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
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
        cardRepository.delete(card);
        downgradeRole(card.getMember());
        inactivePaymentGatewayCard(card);
    }

    private void downgradeRole(Member member) {
        long count = cardRepository.countByMemberId(member.getId());
        if (count == 0) {
            Role freeRole = roleRepository.findByRoleType(RoleType.ROLE_FREE).orElseThrow(RoleNotFoundException::new);
            member.changeRole(RoleType.ROLE_NORMAL, freeRole);
            updateAuthentication(member);
            banForCardDeletionApiKey(member);
        }
    }

    private void banForCardDeletionApiKey(Member member) {
        apiKeyRepository.banForCardDeletionByMemberId(member.getId());
    }

    private void inactivePaymentGatewayCard(Card card) {
        PaymentGatewayType paymentGatewayType = card.getPaymentGateway().getPaymentGatewayType();
        paymentGatewayClients.stream()
                .filter(client -> client.supports(paymentGatewayType))
                .findFirst()
                .orElseThrow(UnsupportedPaymentGatewayException::new)
                .inactive(card.getKey());
    }

    private void updateAuthentication(Member member) {
        Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication1 -> authentication1 instanceof OAuth2AuthenticationToken)
                .map(OAuth2AuthenticationToken.class::cast)
                .map(oauth2AuthToken -> new OAuth2AuthenticationToken(
                        oauth2AuthToken.getPrincipal(),
                        member.getMemberRoles().stream()
                                .map(memberRole -> new SimpleGrantedAuthority(memberRole.getRole().getRoleType().name()))
                                .collect(Collectors.toSet()),
                        oauth2AuthToken.getAuthorizedClientRegistrationId()
                ))
                .ifPresentOrElse(
                        SecurityContextHolder.getContext()::setAuthentication,
                        () -> {
                            throw new UpdateAuthenticationFailureException();
                        }
                );
    }
}
