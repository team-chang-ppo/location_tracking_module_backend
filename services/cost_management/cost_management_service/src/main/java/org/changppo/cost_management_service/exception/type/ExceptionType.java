package org.changppo.cost_management_service.exception.type;

import lombok.Getter;

@Getter
public enum ExceptionType {
    EXCEPTION("exception.code", "exception.msg"),
    AUTHENTICATION_ENTRY_POINT_EXCEPTION("authenticationEntryPointException.code", "authenticationEntryPointException.msg"),
    ACCESS_DENIED_EXCEPTION("accessDeniedException.code", "accessDeniedException.msg"),
    BIND_EXCEPTION("bindException.code", "bindException.msg"),
    LOGIN_FAILURE_EXCEPTION("loginFailureException.code", "loginFailureException.msg"),
    MEMBER_NOT_FOUND_EXCEPTION("memberNotFoundException.code", "memberNotFoundException.msg"),
    MEMBER_UNLINK_FAILURE_EXCEPTION("memberUnlinkFailureException.code", "memberUnlinkFailureException.msg"),
    ROLE_NOT_FOUND_EXCEPTION("roleNotFoundException.code", "roleNotFoundException.msg"),
    GRADE_NOT_FOUND_EXCEPTION("gradeNotFoundException.code", "gradeNotFoundException.msg"),
    APIKEY_NOT_FOUND_EXCEPTION("apiKeyNotFoundException.code", "apiKeyNotFoundException.msg"),
    PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION("paymentGatewayNotFoundException.code", "paymentGatewayNotFoundException.msg");

    private final String code;
    private final String message;

    ExceptionType(String code, String message) {
        this.code = code;
        this.message = message;
    }
}