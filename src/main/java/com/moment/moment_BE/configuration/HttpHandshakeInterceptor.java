package com.moment.moment_BE.configuration;

import com.moment.moment_BE.dto.request.IntrospectRequest;
import com.moment.moment_BE.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.HandshakeResponse;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
public class HttpHandshakeInterceptor implements HandshakeInterceptor {


    private final AuthenticationService authenticationService;

    @Autowired
    public HttpHandshakeInterceptor(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String cookieHeader = extractSessionId(
                request.getHeaders().getFirst(HttpHeaders.COOKIE)
        ) ;
        IntrospectRequest introspectRequest = new IntrospectRequest();
        introspectRequest.setToken(cookieHeader);





        if ( !authenticationService.introspect(introspectRequest).isValid()) {
            response.setStatusCode(HttpStatus.FORBIDDEN);
            return false;
        }

        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {

    }

    public  String extractSessionId(String cookieHeader) {
        if (cookieHeader == null || cookieHeader.isEmpty()) {
            return null;
        }

        // Tách chuỗi cookie
        String[] cookies = cookieHeader.split(";");

        // Duyệt qua các cookie để tìm session-id
        for (String cookie : cookies) {
            cookie = cookie.trim();  // Loại bỏ khoảng trắng thừa
            if (cookie.startsWith("session-id=")) {
                return cookie.substring("session-id=".length());  // Trả về giá trị của session-id
            }
        }

        return null;  // Trả về null nếu không tìm thấy session-id
    }
}
