package org.changppo.account.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.account.entity.common.EntityDate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP, name = CONCAT(name, '_', CURRENT_TIMESTAMP) WHERE member_id = ?")
@SQLRestriction("deleted_at is NULL")
public class Member extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String profileImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column
    private String password;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime paymentFailureBannedAt;

    @Column
    private LocalDateTime deletionRequestedAt;

    @Builder
    public Member(String name, String username, String profileImage, Role role) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
        this.password = null;
        this.deletedAt = null;
        this.paymentFailureBannedAt = null;
        this.deletionRequestedAt = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isPaymentFailureBanned() {
        return this.paymentFailureBannedAt != null;
    }

    public boolean isDeletionRequested() {
        return this.deletionRequestedAt != null;
    }

    public void updateInfo(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public void changeRole(Role role) {
        this.role = role;
    }

    public void banForPaymentFailure(LocalDateTime time) {
        this.paymentFailureBannedAt = time;
    }

    public void unbanForPaymentFailure() {
        this.paymentFailureBannedAt = null;
    }

    public void requestDeletion(LocalDateTime time) {
        this.deletionRequestedAt =time;
    }

    public void cancelDeletionRequest() {
        this.deletionRequestedAt = null;
    }
}
