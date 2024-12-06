package com.moment.moment_BE.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moment.moment_BE.dto.response.ApiResponse;
import com.moment.moment_BE.dto.response.ErrorResponse;
import com.moment.moment_BE.exception.ErrorCode;
import com.moment.moment_BE.exception.AuthErrorCode;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        System.out.println("Authorization Header: " + request.getHeader("Authorization"));
        System.out.println("Origin: " + request.getHeader("Origin"));
        System.out.println("Method: " + request.getMethod());

        ErrorCode errorCode = AuthErrorCode.UNAUTHENTICATED;

//        set 401
        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<?> apiResponse = ApiResponse.builder()
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
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
