package me.redshore.web_gagebu.common.exception;

import lombok.Getter;

@Getter
public class AppBaseException extends RuntimeException {

    private static final long serialVersionUID = 129872L;

    private final ErrorCode errorCode;

    public AppBaseException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppBaseException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

}
