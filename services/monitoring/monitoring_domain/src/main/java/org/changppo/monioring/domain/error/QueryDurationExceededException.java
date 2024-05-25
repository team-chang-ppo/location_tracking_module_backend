package org.changppo.monioring.domain.error;

public class QueryDurationExceededException extends AbstractMonitoringServerException{
    public QueryDurationExceededException() {
        super(ErrorCode.QUERY_DURATION_EXCEEDED);
    }
}
