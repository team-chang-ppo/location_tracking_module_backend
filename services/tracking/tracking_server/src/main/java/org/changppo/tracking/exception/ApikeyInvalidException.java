package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class ApikeyInvalidException extends TrackingBusinessException{
    public ApikeyInvalidException() {
        super(ErrorCode.APIKEY_INVALID);
    }
}
