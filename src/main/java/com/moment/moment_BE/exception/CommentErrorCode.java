package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentErrorCode implements ErrorCode {
    COMMENT_NOT_FOUND("COMMENT_1","Comment not found", HttpStatus.NOT_FOUND),
        COMMENT_DELETE_CONDITION_NOT_MET("COMMENT_2", "Comment cannot be deleted due to current state",HttpStatus.BAD_REQUEST)
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
