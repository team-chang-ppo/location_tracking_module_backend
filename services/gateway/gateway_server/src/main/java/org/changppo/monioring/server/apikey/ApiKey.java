package org.changppo.monioring.server.apikey;

public record ApiKey(
        Long apiKeyId,
        GradeType gradeType,
        Long memberId
) {
}
