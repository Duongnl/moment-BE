package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum PhotoErrorCode implements ErrorCode {
    SAVE_PHOTO_FAIL("PHOTO_1","Save photo is fail", HttpStatus.BAD_REQUEST),
    ;



    PhotoErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
