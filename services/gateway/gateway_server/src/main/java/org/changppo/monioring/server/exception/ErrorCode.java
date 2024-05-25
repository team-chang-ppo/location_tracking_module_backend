package org.changppo.monioring.server.exception;

import lombok.Getter;
import org.changppo.commons.FailedResponseBody;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Gateway
    API_KEY_NOT_FOUND("G0001", "API Key not found", HttpStatus.UNAUTHORIZED),
    INVALID_API_KEY("G0002", "Invalid API Key", HttpStatus.UNAUTHORIZED),
    ILLEGAL_STATE("G0003", "Illegal state", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR("G0004", "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNHANDLED_ERROR("G0005", "Unhandled error", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public FailedResponseBody<?> toFailedResponseBody() {
        return new FailedResponseBody<>(code, message);
    }
}
