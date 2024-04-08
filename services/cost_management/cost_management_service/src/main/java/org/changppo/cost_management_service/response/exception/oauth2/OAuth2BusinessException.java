package org.changppo.cost_management_service.response.exception.oauth2;

import org.changppo.cost_management_service.response.exception.common.BusinessException;
import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public abstract class OAuth2BusinessException extends BusinessException {

    public OAuth2BusinessException(ExceptionType exceptionType, Throwable cause) {
        super(exceptionType, cause);
    }
}
