package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class UnexpectedServerErrorException extends TrackingBusinessException{
    public UnexpectedServerErrorException() {
        super(ErrorCode.UNEXPECTED_SERVER_ERROR);
    }
}
