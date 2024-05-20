package org.changppo.monioring.domain.error;

public class RemoteSessionFetchFailedException extends AbstractMonitoringServerException {

    public RemoteSessionFetchFailedException() {
        super(ErrorCode.REMOTE_SESSION_FETCH_FAILED);
    }
}
