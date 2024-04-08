package org.changppo.cost_management_service.entity.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.card.Card;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.changppo.cost_management_service.entity.member.Member;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")
@SQLRestriction("deleted_at is NULL")
public class Payment extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(name = "`key`", unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private Integer amount;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Payment(String key, Integer amount, Card card, Member member, LocalDateTime startedAt, LocalDateTime endedAt) {
        this.key = key;
        this.amount = amount;
        this.card = card;
        this.member = member;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.deletedAt = null;
    }
}