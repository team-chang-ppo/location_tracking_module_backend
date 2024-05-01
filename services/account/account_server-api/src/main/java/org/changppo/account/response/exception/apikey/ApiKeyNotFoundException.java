package org.changppo.account.response.exception.apikey;

import org.changppo.account.response.exception.common.ExceptionType;

public class ApiKeyNotFoundException extends ApiKeyBusinessException {
    public ApiKeyNotFoundException() {
        super(ExceptionType.APIKEY_NOT_FOUND_EXCEPTION);
    }
}
