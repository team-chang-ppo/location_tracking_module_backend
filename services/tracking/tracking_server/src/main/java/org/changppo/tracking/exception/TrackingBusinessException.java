package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.BusinessException;
import org.changppo.tracking.exception.common.ErrorCode;

public class TrackingBusinessException extends BusinessException {
    public TrackingBusinessException(ErrorCode errorCode) {
        super(errorCode);
    }
}
