package org.changppo.account.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.changppo.account.type.RoleType;

import java.util.Set;

@Data
@AllArgsConstructor
public class PrincipalDto {
    private Long memberId;
    private Set<RoleType> roles;
}
