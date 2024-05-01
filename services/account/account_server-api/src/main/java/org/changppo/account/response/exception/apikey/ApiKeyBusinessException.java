package org.changppo.account.response.exception.apikey;

import org.changppo.account.response.exception.common.BusinessException;
import org.changppo.account.response.exception.common.ExceptionType;

public abstract class ApiKeyBusinessException extends BusinessException {

    public ApiKeyBusinessException(ExceptionType exceptionType) {
        super(exceptionType);
    }
}
