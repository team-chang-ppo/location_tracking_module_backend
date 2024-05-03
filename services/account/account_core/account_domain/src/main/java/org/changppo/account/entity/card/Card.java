package org.changppo.account.entity.card;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.account.entity.common.EntityDate;
import org.changppo.account.entity.member.Member;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE card SET deleted_at = CURRENT_TIMESTAMP WHERE card_id = ?")
@SQLRestriction("deleted_at is NULL")
public class Card extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "card_id")
    private Long id;

    @Column(name = "`key`", unique = true, nullable = false)
    private String key;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String acquirerCorporation;

    @Column(nullable = false)
    private String issuerCorporation;

    @Column(nullable = false)
    private String bin;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_gateway_id", nullable = false)
    private PaymentGateway paymentGateway;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Card(String key, String type, String acquirerCorporation, String issuerCorporation, String bin, PaymentGateway paymentGateway, Member member) {
        this.key = key;
        this.type = type;
        this.acquirerCorporation = acquirerCorporation;
        this.issuerCorporation = issuerCorporation;
        this.bin = bin;
        this.paymentGateway = paymentGateway;
        this.member = member;
        this.deletedAt = null;
    }
}
