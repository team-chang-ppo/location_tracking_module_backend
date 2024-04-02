package org.changppo.cost_management_service.exception.apikey;

import org.changppo.cost_management_service.exception.common.BusinessException;
import org.changppo.cost_management_service.exception.common.ExceptionType;

public class ApiKeyBusinessException extends BusinessException {

    public ApiKeyBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
