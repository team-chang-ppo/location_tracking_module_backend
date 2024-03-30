package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class JwtTokenInvalidException extends JwtBusinessException{
    public JwtTokenInvalidException() {
        super(ErrorCode.JWT_INVALID);
    }

    public JwtTokenInvalidException(Throwable cause) {
        super(cause, ErrorCode.JWT_INVALID);
    }
}
