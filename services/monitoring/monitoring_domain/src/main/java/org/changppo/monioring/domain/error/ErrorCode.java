package org.changppo.monioring.domain.error;

import lombok.Getter;
import org.changppo.commons.FailedResponseBody;

@Getter
public enum ErrorCode {
    ACCESS_DENIED("M0001", "Access Denied", 401),
    INVALID_INPUT_VALUE("M0002", "Invalid Input Value", 400),
    INTERNAL_SERVER_ERROR("M0003", "Internal Server Error", 500),
    HTTP_MEDIA_TYPE_NOT_ACCEPTABLE("M0004", "Http Media Type Not Acceptable", 406),
    REMOTE_SESSION_FETCH_FAILED("M0005", "Remote Session Fetch Failed", 500),
    VALID_AUTHENTICATION_REQUIRED("M0006", "Valid Authentication Required", 401),
    ;
    private final String code;
    private final String message;
    private final int responseStatus;

    ErrorCode(String code, String message, int responseStatus) {
        this.code = code;
        this.message = message;
        this.responseStatus = responseStatus;
    }

    public FailedResponseBody<?> toResponse() {
        return new FailedResponseBody<>(code, message);
    }
}
