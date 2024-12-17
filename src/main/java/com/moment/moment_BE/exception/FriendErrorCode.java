package com.moment.moment_BE.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum FriendErrorCode implements ErrorCode {
    FRIEND_NOT_FOUND_PENDING("FRIEND_1","Not found pending", HttpStatus.BAD_REQUEST),
    FRIEND_ACCEPTED("FRIEND_2","Accepted", HttpStatus.BAD_REQUEST),
    FRIEND_BLOCKED("FRIEND_3","Blocked", HttpStatus.BAD_REQUEST),
    FRIEND_PENDING("FRIEND_4","Pending", HttpStatus.BAD_REQUEST),
    FRIEND_ERROR("FRIEND_5","Error", HttpStatus.BAD_REQUEST),
    FRIEND_NOT_FOUND("FRIEND_6","Not found friend", HttpStatus.BAD_REQUEST);
    FriendErrorCode(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    private String code;
    private String message;
    private HttpStatus httpStatus;
}
