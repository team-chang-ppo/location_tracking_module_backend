package org.changppo.tracking.exception;

import org.changppo.tracking.exception.TrackingBusinessException;
import org.changppo.tracking.exception.common.ErrorCode;

public class TrackingNotFoundException extends TrackingBusinessException {
    public TrackingNotFoundException() {
        super(ErrorCode.TRACKING_NOT_FOUND);
    }
}
