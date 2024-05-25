package org.changppo.monioring.domain.error;

public class InvalidInputValueException extends AbstractMonitoringServerException {
    public InvalidInputValueException() {
        super(ErrorCode.INVALID_INPUT_VALUE);
    }
}
