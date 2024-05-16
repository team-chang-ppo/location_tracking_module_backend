package org.changppo.account.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.account.entity.common.EntityDate;
import org.changppo.account.type.RoleType;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private final Set<MemberRole> memberRoles = new HashSet<>();

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime paymentFailureBannedAt; // TODO. 정기 결제 실패로 인한 정지.

    @Column
    private LocalDateTime deletionRequestedAt; // TODO. 회원 탈퇴 요청.

    @Builder
    public Member(String name, String username, String profileImage,  Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        roles.forEach(role -> this.memberRoles.add(new MemberRole(this, role)));
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

    public void changeRole(RoleType fromRoleType, Role toRole) {
        this.memberRoles.stream()
                .filter(memberRole -> memberRole.getRole().getRoleType() == fromRoleType)
                .findFirst()
                .ifPresent(memberRole -> {
                    this.memberRoles.remove(memberRole);
                    this.memberRoles.add(new MemberRole(this, toRole));
                });
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
