package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum InValidErrorCode implements  ErrorCode{
    USER_NAME_INVALID("INVALID_1","Invalid user name", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID("INVALID_2", "Invalid password", HttpStatus.BAD_REQUEST),
    EMAIL_INVALID ("INVALID_3", "Invalid email address", HttpStatus.BAD_REQUEST),
    SEX_INVALID ("INVALID_4", "Invalid sex", HttpStatus.BAD_REQUEST),
    DATE_INVALID ("INVALID_5", "Invalid date", HttpStatus.BAD_REQUEST),
    NAME_INVALID ("INVALID_6", "Invalid name", HttpStatus.BAD_REQUEST),
    JSON_INVALID ("INVALID_7", "Invalid json", HttpStatus.BAD_REQUEST),
    NOT_BLANK ("INVALID_8", "Invalid blank", HttpStatus.BAD_REQUEST),
    NOT_NULL ("INVALID_9", "Invalid null", HttpStatus.BAD_REQUEST),

    ;

    InValidErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;

}
