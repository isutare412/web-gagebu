package me.redshore.web_gagebu.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppBaseException.class)
    public ResponseEntity<ErrorResponse> handleAppBaseException(AppBaseException ex) {
        var errorCode = ex.getErrorCode();
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());
        var httpStatus = errorCode.toHttpStatus();

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handlNoResourceFoundException(NoResourceFoundException ex) {
        var httpStatus = HttpStatus.NOT_FOUND;
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ErrorResponse> handleHttpStatusCodeException(HttpStatusCodeException ex) {
        var httpStatus = toHttpStatus(ex.getStatusCode().value());
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        var httpStatus = HttpStatus.UNAUTHORIZED;
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        var httpStatus = HttpStatus.FORBIDDEN;
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedOperationException(UnsupportedOperationException ex) {
        var message = ex.getMessage();
        if (message == null || message.isBlank()) {
            message = "This operation is not supported";
        }

        var httpStatus = HttpStatus.NOT_IMPLEMENTED;
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        var errorCode = ErrorCode.ofHttpStatus(httpStatus);
        var errorResponse = new ErrorResponse(errorCode, ex.getMessage());

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private static void logErrorResponse(HttpStatus httpStatus, Exception ex) {
        if (httpStatus.is4xxClientError()) {
            log.warn(String.format("4xx client error occurred: %d %s - %s",
                                   httpStatus.value(), httpStatus.getReasonPhrase(),
                                   ex.getMessage()));
        } else if (httpStatus.is5xxServerError()) {
            log.error(String.format("5xx server error occurred: %d %s - %s",
                                    httpStatus.value(), httpStatus.getReasonPhrase(),
                                    ex.getMessage()),
                      ex);
        }
    }

    private static HttpStatus toHttpStatus(int statusCode) {
        HttpStatus httpStatus;
        try {
            httpStatus = HttpStatus.valueOf(statusCode);
        } catch (IllegalArgumentException ex) {
            log.error("Unexpected http status code: {}. Converting to default status code",
                      statusCode, ex);
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return httpStatus;
    }

}
