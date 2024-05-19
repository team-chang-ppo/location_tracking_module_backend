package org.changppo.account.repository.card;

import org.changppo.account.entity.card.Card;
import org.changppo.account.service.dto.card.CardDto;

import java.util.List;

public interface QuerydslCardRepository {
    List<Card> findAllCardByMemberIdOrderByAsc(Long memberId);
    long countByMemberId(Long memberId);
    void deleteAllByMemberId(Long memberId);
    List<CardDto> findAllByMemberIdOrderByAsc(Long memberId);
}
