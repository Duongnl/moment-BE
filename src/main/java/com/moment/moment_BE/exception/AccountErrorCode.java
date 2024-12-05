package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum AccountErrorCode implements ErrorCode {
    UER_NAME_INVALID("ACCOUNT_1","Uncategorized error", HttpStatus.BAD_REQUEST),
    ;

    AccountErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
