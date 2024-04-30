package org.changppo.monioring.server.apikey;

public record ApiKey(
        Long id,
        GradeType gradeType,
        Long memberId
) {
}
