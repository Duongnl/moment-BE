package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum AccountErrorCode implements ErrorCode {
    USER_NOT_FOUND("ACCOUNT_1","User not found", HttpStatus.NOT_FOUND),
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
