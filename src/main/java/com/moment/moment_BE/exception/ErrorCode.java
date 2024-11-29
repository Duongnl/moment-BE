package com.moment.moment_BE.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public interface ErrorCode {


    public String getCode();

    public String getMessage();

    public HttpStatus getHttpStatus();
}
