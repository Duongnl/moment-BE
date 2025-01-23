package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum NotiViewErrorCode implements ErrorCode {
    SAVE_NOTI_VIEW_FAIL("NOTI_VIEW_1","Save noti view is fail", HttpStatus.BAD_REQUEST),
    ;


    NotiViewErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
