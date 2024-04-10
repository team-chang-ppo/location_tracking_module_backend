package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.BusinessException;
import org.changppo.tracking.exception.common.ErrorCode;

public class RequiredAuthenticationException extends BusinessException {
    public RequiredAuthenticationException() {
        super(ErrorCode.REQUIRED_AUTHENTICATION);
    }
}
