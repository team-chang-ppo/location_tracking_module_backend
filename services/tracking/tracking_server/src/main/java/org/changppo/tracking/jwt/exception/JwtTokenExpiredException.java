package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class JwtTokenExpiredException extends JwtAuthenticationException {
    public JwtTokenExpiredException() {
        super(ErrorCode.JWT_EXPIRED);
    }

    public JwtTokenExpiredException(Throwable cause) {
        super(cause, ErrorCode.JWT_EXPIRED);
    }
}
