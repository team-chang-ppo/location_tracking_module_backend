package org.changppo.cost_management_service.entity.apikey;

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
@SQLDelete(sql = "UPDATE api_key SET deleted_at = CURRENT_TIMESTAMP WHERE api_key_id = ?")
@SQLRestriction("deleted_at is NULL")
public class ApiKey extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "api_key_id")
    private Long id;

    @Column(name = "`value`", unique = true, nullable = false)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_id", nullable = false)
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column
    private LocalDateTime deletedAt;

    // 정지 상태를 나누어야 다른 사유로 인한 정지가 해제되지 않도록 할 수 있음.
    @Column
    private LocalDateTime paymentFailureBannedAt; // TODO. 정기 결제 실패로 인한 정지.

    @Column
    private LocalDateTime cardDeletionBannedAt; // TODO. 카드 삭제로 인한 유료키 정지.

    @Builder
    public ApiKey(String value, Grade grade, Member member) {
        this.value = value;
        this.grade = grade;
        this.member = member;
        this.deletedAt = null;
        this.paymentFailureBannedAt = null;
        this.cardDeletionBannedAt = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isPaymentFailureBanned() {
        return this.paymentFailureBannedAt != null;
    }

    public boolean isCardDeletionBanned() {
        return this.cardDeletionBannedAt != null;
    }

    public void updateValue(String value){
        this.value = value;
    }

    public void banForPaymentFailure(LocalDateTime time) {
        this.paymentFailureBannedAt = time;
    }

    public void unbanForPaymentFailure() {
        this.paymentFailureBannedAt = null;
    }

    public void banForCardDeletion(LocalDateTime time) {
        this.cardDeletionBannedAt = time;
    }

    public void unbanForCardDeletion() {
        this.cardDeletionBannedAt = null;
    }
}
