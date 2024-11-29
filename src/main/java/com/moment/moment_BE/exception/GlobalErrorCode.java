package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum GlobalErrorCode implements ErrorCode{
    UNCATEGORIZED_EXCEPTION("GLOBAL_1","Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNAUTHENTICATED("GLOBAL_2","Unauthenticated", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("GLOBAL_3","User not found", HttpStatus.NOT_FOUND),

    ;

    GlobalErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;


}
