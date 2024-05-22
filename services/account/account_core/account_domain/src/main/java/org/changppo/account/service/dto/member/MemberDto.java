package org.changppo.account.service.dto.member;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Data;
import org.changppo.account.type.RoleType;

import java.time.LocalDateTime;

@Data
public class MemberDto {
    private Long id;
    private String name;
    private String username;
    private String profileImage;
    private RoleType role;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime paymentFailureBannedAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @QueryProjection
    public MemberDto(Long id, String name, String username, String profileImage, RoleType role, LocalDateTime paymentFailureBannedAt, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.profileImage = profileImage;
        this.role = role;
        this.paymentFailureBannedAt = paymentFailureBannedAt;
        this.createdAt = createdAt;
    }
}
