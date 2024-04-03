package org.changppo.cost_management_service.repository.card;

import org.changppo.cost_management_service.dto.card.CardDto;
import org.changppo.cost_management_service.entity.card.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Query("select c from Card c join fetch c.member where c.id = :id")
    Optional<Card> findByIdWithMember(@Param("id") Long id);

    @Query("select c from Card c join fetch c.paymentGateway where c.id = :id")
    Optional<Card> findByIdWithPaymentGateway(@Param("id") Long id);

    @Query("select new org.changppo.cost_management_service.dto.card.CardDto(c.id, c.type, c.issuerCorporation, c.bin, c.paymentGateway.paymentGatewayType, c.createdAt) " +
            "from Card c where c.member.id = :memberId " +
            "order by c.id asc")
    List<CardDto> findAllByMemberId(@Param("memberId") Long memberId);

    long countByMemberId(Long memberId);

    void deleteAllByMemberId(Long memberId);
}
