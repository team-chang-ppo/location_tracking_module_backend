package org.changppo.account.repository.card;

import org.changppo.account.entity.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> , QuerydslCardRepository{
    Optional<Card> findByKey(String key);
}
