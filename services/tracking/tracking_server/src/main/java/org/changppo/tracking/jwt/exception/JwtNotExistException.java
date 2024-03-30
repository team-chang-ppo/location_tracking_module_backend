package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class JwtNotExistException extends JwtBusinessException{
    public JwtNotExistException() {
        super(ErrorCode.JWT_NOT_EXIST);
    }
}
