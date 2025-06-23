package me.redshore.web_gagebu.common.exception;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {

    private final int status;

    private final String statusText;

    private final ErrorCode errorCode;

    private String message;

}
