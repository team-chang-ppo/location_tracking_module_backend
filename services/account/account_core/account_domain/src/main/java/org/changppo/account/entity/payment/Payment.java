package org.changppo.account.entity.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.account.entity.common.EntityDate;
import org.changppo.account.entity.member.Member;
import org.changppo.account.type.PaymentStatus;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
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

    @Column(nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private LocalDateTime startedAt;

    @Column(nullable = false)
    private LocalDateTime endedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Embedded
    private PaymentCardInfo cardInfo;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Payment(BigDecimal amount, PaymentStatus status, LocalDateTime startedAt, LocalDateTime endedAt, Member member, PaymentCardInfo cardInfo) {
        this.amount = amount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.member = member;
        this.cardInfo = cardInfo;
        this.deletedAt = null;
    }

    public void changeStatus(PaymentStatus status, PaymentCardInfo cardInfo) {
        this.status = status;
        this.cardInfo = cardInfo;
    }

    public void delete(LocalDateTime time) {
        this.deletedAt = time;
    }
}
