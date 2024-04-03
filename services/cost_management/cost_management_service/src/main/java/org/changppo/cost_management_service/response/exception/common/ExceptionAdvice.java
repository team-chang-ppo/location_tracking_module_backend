package org.changppo.cost_management_service.response.exception.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.response.exception.oauth2.OAuth2BusinessException;
import org.changppo.cost_management_service.response.exception.paymentgateway.PaymentGatewayBusinessException;
import org.changppo.cost_management_service.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response> businessException(BusinessException e) {
        log.info("e = {}", e.getMessage());
        ExceptionType exceptionType = e.getExceptionType();
        return ResponseEntity.status(exceptionType.getStatus())
                .body(responseHandler.getFailureResponse(exceptionType));
    }

    @ExceptionHandler(OAuth2BusinessException.class)
    public ResponseEntity<Response> oauth2BusinessException(OAuth2BusinessException e) {
        log.error("Payment gateway exception occurred: {}, Cause: {}", e.getMessage(), e.getCause() != null ? e.getCause().toString() : "No cause available");
        // TODO. Admin에게 알림
        return ResponseEntity
                .status(e.getExceptionType().getStatus())
                .body(responseHandler.getFailureResponse(e.getExceptionType()));
    }

    @ExceptionHandler(PaymentGatewayBusinessException.class)
    public ResponseEntity<Response> paymentGatewayBusinessException(PaymentGatewayBusinessException e) {
        log.error("Payment gateway exception occurred: {}, Cause: {}", e.getMessage(), e.getCause() != null ? e.getCause().toString() : "No cause available");
        // TODO. Admin에게 알림
        return ResponseEntity
                .status(e.getExceptionType().getStatus())
                .body(responseHandler.getFailureResponse(e.getExceptionType()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> exception(Exception e) {
        log.info("e = {}", e.getMessage());
        return ResponseEntity
                .status(ExceptionType.EXCEPTION.getStatus())
                .body(responseHandler.getFailureResponse(ExceptionType.EXCEPTION));
    }

    @ExceptionHandler(AccessDeniedException.class) // @PreAuthorize으로 부터 발생하는 오류
    public ResponseEntity<Response> accessDeniedException(AccessDeniedException e) {
        return ResponseEntity
                .status(ExceptionType.ACCESS_DENIED_EXCEPTION.getStatus())
                .body(responseHandler.getFailureResponse(ExceptionType.ACCESS_DENIED_EXCEPTION));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(ExceptionType.BIND_EXCEPTION.getStatus())
                .body(responseHandler.getFailureResponse(ExceptionType.BIND_EXCEPTION, e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }
}