package me.redshore.web_gagebu.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "Common error response structure")
@Getter
public class ErrorResponse {

    @Schema(example = "404")
    private final int status;

    @Schema(example = "Not Found")
    private final String statusText;

    @Schema(example = "DETAILED_ERROR_CODE")
    private final ErrorCode errorCode;

    @Schema(example = "Something went wrong")
    private final String message;

    public ErrorResponse(ErrorCode errorCode, String message) {
        var httpStatus = errorCode.toHttpStatus();

        this.status = httpStatus.value();
        this.statusText = httpStatus.getReasonPhrase();
        this.errorCode = errorCode;
        this.message = message;
    }

}
