package org.changppo.gateway.apikey;

public record ApiKey(
        Long id,
        GradeType gradeType,
        Long memberId
) {
}
