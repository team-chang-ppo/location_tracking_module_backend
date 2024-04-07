package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class TrackingAlreadyExitedException extends TrackingBusinessException{
    public TrackingAlreadyExitedException() {
        super(ErrorCode.TRACKING_ALREADY_EXITED);
    }
}
