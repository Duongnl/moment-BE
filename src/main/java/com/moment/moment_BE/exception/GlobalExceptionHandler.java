package com.moment.moment_BE.exception;

import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.ArrayList;
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


    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    ResponseEntity<ApiResponse> handlingHttpMessage(HttpMessageNotReadableException exception){

        return  ResponseEntity.badRequest().body(ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errors(
                        List.of(
                                ErrorResponse.builder()
                                        .code(InValidErrorCode.JSON_INVALID.getCode())
                                        .message(exception.getMessage())
                                        .build()
                        )
                )
                .build()
        );
    }


    //    DefaultHandlerExceptionResolver
    @ExceptionHandler( value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){

        List<ErrorResponse> errorResponses = new ArrayList<>();
        exception.getBindingResult().getAllErrors().forEach(exp -> {
            ErrorCode errorCode =  InValidErrorCode.valueOf(exp.getDefaultMessage());

            errorResponses.add(ErrorResponse.builder()
                    .code(errorCode.getCode())
                    .message(errorCode.getMessage())
                    .build());
        });

        return  ResponseEntity.badRequest().body(ApiResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .message(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .errors(errorResponses)
                .build()
        );
    }


}
