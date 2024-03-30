package org.changppo.tracking.jwt.exception;

import org.changppo.tracking.exception.common.BusinessException;
import org.changppo.tracking.exception.common.ErrorCode;

public class JwtBusinessException extends BusinessException {
    public JwtBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }

    public JwtBusinessException(Throwable cause,ErrorCode errorCode) {
        super(errorCode, cause);
    }
}
