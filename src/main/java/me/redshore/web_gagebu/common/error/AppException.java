package me.redshore.web_gagebu.common.error;

import java.io.Serial;
import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 129872L;

    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }
}
