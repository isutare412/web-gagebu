package me.redshore.web_gagebu.common.error;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed error codes for the application")
public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    UNEXPECTED_IDP(HttpStatus.BAD_REQUEST),

    /**
     * 401 Unauthorized
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),

    /**
     * 403 Forbidden
     */
    FORBIDDEN(HttpStatus.FORBIDDEN),

    /**
     * 404 Not Found
     */
    NOT_FOUND(HttpStatus.NOT_FOUND),

    /**
     * 405 Method Not Allowed
     */
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),

    /**
     * 409 Conflict
     */
    CONFLICT(HttpStatus.CONFLICT),

    /**
     * 422 Unprocessable Entity
     */
    UNPROCESSABLE_ENTITY(HttpStatus.UNPROCESSABLE_ENTITY),

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

    /**
     * 501 Not Implemented
     */
    NOT_IMPLEMENTED(HttpStatus.NOT_IMPLEMENTED),

    /**
     * 503 Service Unavailable
     */
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE);

    private static Map<HttpStatus, ErrorCode> ERROR_CODE_BY_STATUS = new HashMap<>();

    private final HttpStatus httpStatus;

    static {
        for (var errorCode : values()) {
            ERROR_CODE_BY_STATUS.putIfAbsent(errorCode.httpStatus, errorCode);
        }
    }

    ErrorCode(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public static ErrorCode ofHttpStatus(HttpStatus httpStatus) {
        return ERROR_CODE_BY_STATUS.getOrDefault(httpStatus, INTERNAL_SERVER_ERROR);
    }

    public HttpStatus toHttpStatus() {
        return this.httpStatus;
    }

}
