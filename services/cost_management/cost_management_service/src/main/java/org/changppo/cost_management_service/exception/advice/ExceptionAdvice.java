package org.changppo.cost_management_service.exception.advice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.changppo.cost_management_service.exception.*;
import org.changppo.cost_management_service.exception.response.ResponseHandler;
import org.changppo.cost_management_service.response.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.changppo.cost_management_service.exception.type.ExceptionType.*;


@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ExceptionAdvice {

    private final ResponseHandler responseHandler;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> exception(Exception e) {
        log.info("e = {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseHandler.getFailureResponse(EXCEPTION));
    }

    @ExceptionHandler(AccessDeniedException.class) // @PreAuthorize으로 부터 발생하는 오류
    public ResponseEntity<Response> accessDeniedException(AccessDeniedException e) {
        log.info("e = {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(responseHandler.getFailureResponse(ACCESS_DENIED_EXCEPTION));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(responseHandler.getFailureResponse(BIND_EXCEPTION, e.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }

    @ExceptionHandler(LoginFailureException.class)
    public ResponseEntity<Response> loginFailureException() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(responseHandler.getFailureResponse(LOGIN_FAILURE_EXCEPTION));
    }


    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<Response> memberNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseHandler.getFailureResponse(MEMBER_NOT_FOUND_EXCEPTION));
    }

    @ExceptionHandler(MemberUnlinkFailureException.class)
    public ResponseEntity<Response> memberUnlinkFailureException(Exception e) {
        log.info("e = {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(responseHandler.getFailureResponse(MEMBER_UNLINK_FAILURE_EXCEPTION));
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<Response> roleNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseHandler.getFailureResponse(ROLE_NOT_FOUND_EXCEPTION));
    }

    @ExceptionHandler(GradeNotFoundException.class)
    public ResponseEntity<Response> gradeNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseHandler.getFailureResponse(GRADE_NOT_FOUND_EXCEPTION));
    }

    @ExceptionHandler(ApiKeyNotFoundException.class)
    public ResponseEntity<Response> apiKeyNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseHandler.getFailureResponse(APIKEY_NOT_FOUND_EXCEPTION));
    }

    @ExceptionHandler(PaymentGatewayNotFoundException.class)
    public ResponseEntity<Response> paymentGatewayNotFoundException() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(responseHandler.getFailureResponse(PAYMENT_GATEWAY_NOT_FOUND_EXCEPTION));
    }
}