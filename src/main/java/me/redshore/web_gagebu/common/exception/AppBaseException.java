package me.redshore.web_gagebu.common.exception;

import org.springframework.http.HttpStatus;
import lombok.Getter;

@Getter
public abstract class AppBaseException extends RuntimeException {

    private static final long serialVersionUID = 129872L;

    private final ErrorCode errorCode;

    private final HttpStatus httpStatus;

    public AppBaseException(ErrorCode errorCode, HttpStatus httpStatus, String message,
                            Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public AppBaseException(ErrorCode errorCode, HttpStatus httpStatus, String message) {
        this(errorCode, httpStatus, message, null);
    }

}
