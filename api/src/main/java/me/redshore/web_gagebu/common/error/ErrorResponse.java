package me.redshore.web_gagebu.common.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import me.redshore.web_gagebu.common.filter.RequestIdFilter;
import org.slf4j.MDC;

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

    @Schema(example = "123e4567-e89b-12d3-a456-426614174000")
    private final String requestId;

    public ErrorResponse(ErrorCode errorCode, String message) {
        var httpStatus = errorCode.toHttpStatus();

        this.status = httpStatus.value();
        this.statusText = httpStatus.getReasonPhrase();
        this.errorCode = errorCode;
        this.message = message;
        this.requestId = MDC.get(RequestIdFilter.MDC_REQUEST_ID_KEY);
    }

}
