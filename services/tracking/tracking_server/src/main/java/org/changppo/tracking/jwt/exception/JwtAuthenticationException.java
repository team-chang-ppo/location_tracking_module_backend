package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class JwtAuthenticationException extends JwtBusinessException{
    public JwtAuthenticationException() {
        super(ErrorCode.AUTHORIZED_FAILED);
    }

}
