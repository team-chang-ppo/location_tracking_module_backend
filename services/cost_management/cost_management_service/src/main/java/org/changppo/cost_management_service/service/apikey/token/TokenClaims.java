package org.changppo.cost_management_service.service.apikey.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenClaims {
    private Long id;
    private Long memberId;
    private String gradeType;
}
