package org.changppo.account.service.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.changppo.account.type.RoleType;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class MemberDto {
    private Long id;
    private String name;
    private String username;
    private String profileImage;
    private Set<RoleType> roles;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentFailureBannedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public MemberDto(Long id, String name, String username, String profileImage, Set<RoleType> roles, LocalDateTime paymentFailureBannedAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.roles = roles;
        this.paymentFailureBannedAt = paymentFailureBannedAt;
        this.createdAt = createdAt;
    }
}
