package me.redshore.web_gagebu.common.exception.auth;

import org.springframework.http.HttpStatus;
import me.redshore.web_gagebu.common.exception.AppBaseException;
import me.redshore.web_gagebu.common.exception.ErrorCode;

public class UnexpectedIdpException extends AppBaseException {

    private static final long serialVersionUID = -1985293857989L;

    public UnexpectedIdpException(String message, Throwable cause) {
        super(ErrorCode.UNEXPECTED_IDP, HttpStatus.BAD_REQUEST, message, cause);
    }

    public UnexpectedIdpException(String message) {
        super(ErrorCode.UNEXPECTED_IDP, HttpStatus.BAD_REQUEST, message);
    }

}
