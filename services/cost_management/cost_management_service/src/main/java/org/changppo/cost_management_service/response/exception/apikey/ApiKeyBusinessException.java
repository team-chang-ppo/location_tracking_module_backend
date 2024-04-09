package org.changppo.cost_management_service.response.exception.apikey;

import org.changppo.cost_management_service.response.exception.common.BusinessException;
import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public abstract class ApiKeyBusinessException extends BusinessException {

    public ApiKeyBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
