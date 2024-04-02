package org.changppo.cost_management_service.exception.oauth2;

import org.changppo.cost_management_service.exception.common.BusinessException;
import org.changppo.cost_management_service.exception.common.ExceptionType;

public class OAuth2BusinessException extends BusinessException {

    public OAuth2BusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }

    public OAuth2BusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
