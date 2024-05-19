package org.changppo.utils.jwt.tracking;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TrackingJwtClaims {
    private Long apikeyId;
    private Long memberId;
    private String gradeType;
    private String trackingId;
    private List<String> scopes;

}
