package me.redshore.web_gagebu.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Common error response structure")
@Getter
@Builder
public class ErrorResponse {

    @Schema(example = "404")
    private final int status;

    @Schema(example = "Not Found")
    private final String statusText;

    @Schema(example = "RESOURCE_NOT_FOUND")
    private final ErrorCode errorCode;

    @Schema(example = "Something went wrong")
    private String message;

}
