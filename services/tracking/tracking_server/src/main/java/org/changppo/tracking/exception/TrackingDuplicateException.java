package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class TrackingDuplicateException extends TrackingBusinessException{
    public TrackingDuplicateException() {
        super(ErrorCode.TRACKING_DUPLICATE);
    }
}
