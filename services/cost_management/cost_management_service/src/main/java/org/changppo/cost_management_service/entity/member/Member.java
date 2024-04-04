package org.changppo.cost_management_service.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
@SQLRestriction("deleted_at is NULL")
public class Member extends EntityDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, length = 10)
    private String username;

    @Column(nullable = false)
    private String profileImage;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MemberRole> memberRoles;

    @Column
    private LocalDateTime deletedAt;

    @Column
    private LocalDateTime isPaymentFailureBanned; // TODO. 정기 결제 실패로 인한 정지.

    @Builder
    public Member(String name, String username, String profileImage,  Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.memberRoles = roles.stream().map(r -> new MemberRole(this, r)).collect(toSet());
        this.deletedAt = null;
        this.isPaymentFailureBanned = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public boolean isPaymentFailureBanned() {
        return this.isPaymentFailureBanned != null;
    }

    public void updateInfo(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public void reactivate(String username, String profileImage, Set<Role> roles) {
        this.username = username;
        this.profileImage = profileImage;
        this.deletedAt = null;
        this.memberRoles = roles.stream().map(r -> new MemberRole(this, r)).collect(toSet());
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
        this.isPaymentFailureBanned = time;
    }

    public void unbanForPaymentFailure() {
        this.isPaymentFailureBanned = null;
    }
}
