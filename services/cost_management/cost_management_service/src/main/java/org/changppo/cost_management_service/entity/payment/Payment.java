package org.changppo.cost_management_service.entity.payment;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.changppo.cost_management_service.entity.member.Member;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE payment SET deleted_at = CURRENT_TIMESTAMP WHERE payment_id = ?")  // TODO. member 탈퇴시 삭제
@SQLRestriction("deleted_at is NULL")
public class Payment extends EntityDate {  // TODO. 동시성 문제 고려

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @Column(nullable = false)
    private Integer amount;

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
    public Payment(Integer amount, PaymentStatus status, LocalDateTime startedAt, LocalDateTime endedAt, Member member, PaymentCardInfo cardInfo) {
        this.amount = amount;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.member = member;
        this.cardInfo = cardInfo;
        this.deletedAt = null;
    }

    public void setDeletedAt(LocalDateTime time) {
        this.deletedAt = time;
    }

}