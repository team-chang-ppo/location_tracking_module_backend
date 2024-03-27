package org.changppo.cost_management_service.entity.member;

import jakarta.persistence.*;
import lombok.*;
import org.changppo.cost_management_service.entity.common.EntityDate;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE member SET deleted = true WHERE member_id = ?")
@SQLRestriction("deleted = false")
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

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL , orphanRemoval = true)
    private Set<MemberRole> roles;

    @Column(nullable = false)
    private boolean deleted;

    @Builder
    public Member(String name, String username, String profileImage,  Set<Role> roles) {
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.roles = roles.stream().map(r -> new MemberRole(this, r)).collect(toSet());
        this.deleted = false;
    }

    public void update(String username, String profileImage) {
        this.username = username;
        this.profileImage = profileImage;
    }
}
