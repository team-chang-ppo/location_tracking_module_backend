package org.changppo.account.builder.apikey;

import org.changppo.account.entity.apikey.Grade;
import org.changppo.account.service.dto.apikey.ApiKeyDto;

import java.time.LocalDateTime;

public class ApiKeyDtoBuilder {
    public static ApiKeyDto buildApiKeyDto(Grade grade) {
        return new ApiKeyDto(1L, "testApiKeyValue", grade.getGradeType(), LocalDateTime.now(), LocalDateTime.now(), LocalDateTime.now());
    }
}
