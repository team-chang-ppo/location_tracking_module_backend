package org.changppo.cost_management_service.exception.apikey;

import org.changppo.cost_management_service.exception.common.ExceptionType;

public class ApiKeyNotFoundException extends ApiKeyBusinessException {
    public ApiKeyNotFoundException() {
        super(ExceptionType.APIKEY_NOT_FOUND_EXCEPTION);
    }
}
