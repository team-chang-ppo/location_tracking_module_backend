package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class ApiKeyIdIsNotMatchedException extends TrackingBusinessException {
    public ApiKeyIdIsNotMatchedException() {
        super(ErrorCode.API_KEY_ID_IS_NOT_MATCHED);
    }
}
