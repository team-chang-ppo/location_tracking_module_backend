package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class JwtNotExistException extends JwtAuthenticationException {
    public JwtNotExistException() {
        super(ErrorCode.JWT_NOT_EXIST);
    }
}
