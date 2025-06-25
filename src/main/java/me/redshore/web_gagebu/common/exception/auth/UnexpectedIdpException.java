package me.redshore.web_gagebu.common.exception.auth;

import me.redshore.web_gagebu.common.exception.AppBaseException;
import me.redshore.web_gagebu.common.exception.ErrorCode;

public class UnexpectedIdpException extends AppBaseException {

    private static final long serialVersionUID = -1985293857989L;

    public UnexpectedIdpException(String message, Throwable cause) {
        super(ErrorCode.UNEXPECTED_IDP, message, cause);
    }

    public UnexpectedIdpException(String message) {
        super(ErrorCode.UNEXPECTED_IDP, message);
    }

}
