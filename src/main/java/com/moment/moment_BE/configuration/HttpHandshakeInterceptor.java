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

import java.net.URI;
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
        // Lấy URI từ request
        URI uri = request.getURI();

        // Lấy query parameters từ URI
        String sessionId = getQueryParameter(uri, "ss");

        if (sessionId == null || sessionId.isEmpty()) {
            // Xử lý lỗi, có thể ngừng kết nối WebSocket
            return false;
        }

        try {
            // Lấy username từ token
            String userName = authenticationService.getSubFromToken(sessionId);

            if (userName == null) {
                // Nếu không lấy được userName, từ chối kết nối hoặc trả về lỗi
                return false;
            }

            System.out.println("username >>> " + userName);

            // Lưu vào attributes để sử dụng sau này
            attributes.put("userName", userName);

        } catch (Exception e) {
            // Xử lý exception nếu không lấy được username
            System.err.println("Failed to authenticate token: " + e.getMessage());
            return false; // Từ chối kết nối WebSocket nếu có lỗi
        }

        return true;
    }

    // Phương thức hỗ trợ để lấy query parameter
    private String getQueryParameter(URI uri, String parameterName) {
        String query = uri.getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] keyValue = param.split("=");
                if (keyValue.length == 2 && keyValue[0].equals(parameterName)) {
                    return keyValue[1];
                }
            }
        }
        return null;
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
