package com.moment.moment_BE.exception;

import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

//    handle app exception
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse<?>> handlingAppException (AppException appException){

        ErrorCode errorCode = appException.getErrorCode();

        return ResponseEntity.status(errorCode.getHttpStatus())
                .body(ApiResponse.builder()
                        .status(errorCode.getHttpStatus().value())
                        .message(errorCode.getHttpStatus().getReasonPhrase())
                        .errors(
                                List.of(
                                        ErrorResponse.builder()
                                                .code(errorCode.getCode())
                                                .message(errorCode.getMessage())
                                                .build()
                                )
                        )
                        .build()
                );
    }





}
