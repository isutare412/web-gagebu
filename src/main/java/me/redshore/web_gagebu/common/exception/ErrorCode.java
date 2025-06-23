package me.redshore.web_gagebu.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Detailed error codes for the application")
public enum ErrorCode {

    /**
     * 400 Bad Request
     */
    UNEXPECTED_IDP,

    /**
     * 404 Not Found
     */
    RESOURCE_NOT_FOUND,

    /**
     * 500 Internal Server Error
     */
    INTERNAL_SERVER_ERROR;

}
