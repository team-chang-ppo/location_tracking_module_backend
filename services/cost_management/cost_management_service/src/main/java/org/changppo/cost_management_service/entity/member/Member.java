package org.changppo.cost_management_service.entity.member;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
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

    @OneToMany(mappedBy = "member", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    private Set<MemberRole> roles;

    @Column
    private LocalDateTime deletedAt;

    @Builder
    public Member(String name, String username, String profileImage,  Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.roles = roles.stream().map(r -> new MemberRole(this, r)).collect(toSet());
        this.deletedAt = null;
    }

    public boolean isDeleted() {
        return this.deletedAt != null;
    }

    public void update(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }

    public void reactivate(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
        this.deletedAt = null;
    }
}
