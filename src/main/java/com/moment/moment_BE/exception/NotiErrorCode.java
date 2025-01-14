package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotiErrorCode implements ErrorCode{

    SAVE_NOTI_FAIL("NOTI_1","Save noti is fail", HttpStatus.BAD_REQUEST)
    ;



    NotiErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
