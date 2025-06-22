package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND("COMMENT_1","Comment not found", HttpStatus.NOT_FOUND)
    ;

    CommentErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
