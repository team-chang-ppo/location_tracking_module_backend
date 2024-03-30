package org.changppo.tracking.exception;

import org.changppo.tracking.exception.common.ErrorCode;

public class CoordinatesNotFoundException extends TrackingBusinessException{
    public CoordinatesNotFoundException() {
        super(ErrorCode.COORDINATES_NOT_FOUND);
    }
}
