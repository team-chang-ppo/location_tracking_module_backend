package org.changppo.gateway.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Gateway
    API_KEY_NOT_FOUND("G0001", "API Key not found", HttpStatus.UNAUTHORIZED),
    INVALID_API_KEY("G0002", "Invalid API Key", HttpStatus.UNAUTHORIZED);


    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
