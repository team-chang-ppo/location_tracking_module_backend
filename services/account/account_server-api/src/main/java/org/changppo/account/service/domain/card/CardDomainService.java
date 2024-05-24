package org.changppo.account.service.domain.card;

import lombok.RequiredArgsConstructor;
import org.changppo.account.entity.card.Card;
import org.changppo.account.entity.card.PaymentGateway;
import org.changppo.account.entity.member.Member;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.service.dto.card.CardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true, propagation = Propagation.MANDATORY)
public class CardDomainService {

    private final CardRepository cardRepository;

    @Transactional(propagation = Propagation.MANDATORY)
    public Card createCard(String key, String type, String acquirerCorporation, String issuerCorporation, String bin, PaymentGateway paymentGateway, Member member) {
        Card card = Card.builder()
                .key(key)
                .type(type)
                .acquirerCorporation(acquirerCorporation)
                .issuerCorporation(issuerCorporation)
                .bin(bin)
                .paymentGateway(paymentGateway)
                .member(member)
                .build();
        return cardRepository.save(card);
    }

    public Card getCard(Long id) {
        return cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
    }

    public List<CardDto> getCardDtos(Long memberId) {
        return cardRepository.findAllDtosByMemberIdOrderByAsc(memberId);
    }

    public boolean isLastCard(Long memberId) {  // TODO. 동시성 문제 확인
        return cardRepository.countByMemberId(memberId) == 0;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void deleteCard(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
        cardRepository.delete(card);
    }

    public Page<CardDto> getCardDtos(Pageable pageable) {
        return cardRepository.findAllDtos(pageable);
    }
}
