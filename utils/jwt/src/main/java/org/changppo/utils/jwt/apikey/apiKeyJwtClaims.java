package org.changppo.utils.jwt.apikey;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class apiKeyJwtClaims {
    private Long apikeyId;
    private Long memberId;
    private String gradeType;
}
