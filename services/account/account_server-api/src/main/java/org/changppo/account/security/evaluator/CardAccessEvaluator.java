package org.changppo.account.security.evaluator;

import lombok.RequiredArgsConstructor;
import org.changppo.account.card.Card;
import org.changppo.account.type.RoleType;
import org.changppo.account.repository.card.CardRepository;
import org.changppo.account.response.exception.card.CardNotFoundException;
import org.changppo.account.security.PrincipalHandler;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CardAccessEvaluator extends Evaluator {

    private final CardRepository cardRepository;
    private static final List<RoleType> roleTypes = List.of(RoleType.ROLE_ADMIN);

    @Override
    protected List<RoleType> getRoleTypes() {
        return roleTypes;
    }

    @Override
    protected boolean isEligible(Long id) {
        Card card = cardRepository.findById(id).orElseThrow(CardNotFoundException::new);
        return card.getMember().getId().equals(PrincipalHandler.extractId());
    }
}