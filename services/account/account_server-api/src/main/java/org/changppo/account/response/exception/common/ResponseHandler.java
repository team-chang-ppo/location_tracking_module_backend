package org.changppo.account.response.exception.common;

import lombok.RequiredArgsConstructor;
import org.changppo.account.response.Response;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseHandler {

    private final MessageSource messageSource;

    public Response getFailureResponse(ExceptionType exceptionType) {
        return org.changppo.account.response.Response.failure(getCode(exceptionType.getCode()), getMessage(exceptionType.getMessage()));
    }

    public Response getFailureResponse(ExceptionType exceptionType, Object... args) {
        return Response.failure(getCode(exceptionType.getCode()), getMessage(exceptionType.getMessage(), args));
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
