package org.changppo.account.builder.apikey;

import org.changppo.account.entity.apikey.ApiKey;
import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.entity.member.Member;

public class ApiKeyBuilder {
    public static ApiKey buildApiKey(Grade grade, Member member) {
        return ApiKey.builder()
                .value("testApiKeyValue")
                .grade(grade)
                .member(member)
                .build();
    }

    public static ApiKey buildApiKey(String value, Grade grade, Member member) {
        return ApiKey.builder()
                .value(value)
                .grade(grade)
                .member(member)
                .build();
    }
}
