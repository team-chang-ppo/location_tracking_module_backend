package org.changppo.monioring.domain.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class AbstractMonitoringServerException extends RuntimeException {
    private final ErrorCode errorCode;

}
