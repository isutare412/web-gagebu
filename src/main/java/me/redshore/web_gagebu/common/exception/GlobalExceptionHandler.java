package me.redshore.web_gagebu.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AppBaseException.class)
    public ResponseEntity<ErrorResponse> handleAppBaseException(AppBaseException ex) {
        var httpStatus = ex.getHttpStatus();
        var errorResponse = ErrorResponse.builder()
            .status(httpStatus.value())
            .statusText(httpStatus.getReasonPhrase())
            .errorCode(ex.getErrorCode())
            .message(ex.getMessage())
            .build();

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(Exception ex) {
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        var errorResponse = ErrorResponse.builder()
            .status(httpStatus.value())
            .statusText(httpStatus.getReasonPhrase())
            .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
            .message("An unexpected error occurred")
            .build();

        logErrorResponse(httpStatus, ex);
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    private static void logErrorResponse(HttpStatus httpStatus, Exception ex) {
        if (httpStatus.is4xxClientError()) {
            log.warn(String.format("4xx client error occurred: %d %s - %s",
                                   httpStatus.value(), httpStatus.getReasonPhrase(),
                                   ex.getMessage()),
                     ex);
        } else if (httpStatus.is5xxServerError()) {
            log.error(String.format("5xx server error occurred: %d %s - %s",
                                    httpStatus.value(), httpStatus.getReasonPhrase(),
                                    ex.getMessage()),
                      ex);
        }
    }

}
