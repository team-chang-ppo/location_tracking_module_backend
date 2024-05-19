package org.changppo.monioring.domain.error;

import org.changppo.monioring.domain.response.Response;

public enum ErrorCode {
    ACCESS_DENIED("M0001", "Access Denied"),
    INVALID_INPUT_VALUE("M0002", "Invalid Input Value"),
    ;
    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public Response toResponse() {
        return Response.failure(code, message);
    }
}
