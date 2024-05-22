package org.changppo.account.response.exception.common;

import lombok.RequiredArgsConstructor;
import org.changppo.commons.FailedResponseBody;
import org.changppo.commons.FailedResponseBody.ErrorPayload;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseHandler {

    private final MessageSource messageSource;

    public FailedResponseBody<ErrorPayload> getFailureResponse(ExceptionType exceptionType) {
        return new FailedResponseBody<>(getCode(exceptionType.getCode()), getMessage(exceptionType.getMessage()));
    }

    public FailedResponseBody<ErrorPayload> getFailureResponse(ExceptionType exceptionType, Object... args) {
        return new FailedResponseBody<>(getCode(exceptionType.getCode()), getMessage(exceptionType.getMessage(), args));
    }

    private String getCode(String key) {
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key) {
        return messageSource.getMessage(key,null, LocaleContextHolder.getLocale());
    }

    private String getMessage(String key, Object... args) {
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}
