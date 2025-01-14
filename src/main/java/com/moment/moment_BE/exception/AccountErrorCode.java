package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum AccountErrorCode implements ErrorCode {
    USER_NOT_FOUND("ACCOUNT_1","User not found", HttpStatus.NOT_FOUND),
    USER_NAME_EXISTED("ACCOUNT_2","Account existed", HttpStatus.BAD_REQUEST),
    SAVE_USER_FAIL("ACCOUNT_3","Save user is fail", HttpStatus.BAD_REQUEST),
    OLD_PASSWORD_INCORRECT("ACCOUNT_4", "Old password is incorrect", HttpStatus.BAD_REQUEST),
    SAVE_PASSWORD_FAIL("ACCOUNT_5", "Failed to save password", HttpStatus.INTERNAL_SERVER_ERROR),
    NEW_PASSWORD_SAME_AS_OLD("ACCOUNT_6", "New password cannot be the same as old password", HttpStatus.BAD_REQUEST)
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
