package org.changppo.monitoring.exception;

import lombok.extern.slf4j.Slf4j;
import org.changppo.monioring.domain.error.ErrorCode;
import org.changppo.monioring.domain.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Response> handleIllegalArgumentException(IllegalArgumentException e) {
        log.debug("IllegalArgumentException", e);
        return ResponseEntity.badRequest().body(ErrorCode.INVALID_INPUT_VALUE.toResponse());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Response> handleIllegalStateException(IllegalStateException e) {
        log.error("IllegalStateException", e);
        return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.toResponse());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception e) {
        log.error("Unknown Exception", e);
        return ResponseEntity.internalServerError().body(ErrorCode.INTERNAL_SERVER_ERROR.toResponse());
    }

}
