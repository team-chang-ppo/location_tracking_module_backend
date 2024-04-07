package org.changppo.cost_management_service.response.exception.apikey;

import org.changppo.cost_management_service.response.exception.common.ExceptionType;

public class ApiKeyNotFoundException extends ApiKeyBusinessException {
    public ApiKeyNotFoundException() {
        super(ExceptionType.APIKEY_NOT_FOUND_EXCEPTION);
    }
}
