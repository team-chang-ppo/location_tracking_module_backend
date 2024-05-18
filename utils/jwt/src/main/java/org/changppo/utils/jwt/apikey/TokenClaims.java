package org.changppo.utils.jwt.apikey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenClaims {
    private Long id;
    private Long memberId;
    private String gradeType;
}
