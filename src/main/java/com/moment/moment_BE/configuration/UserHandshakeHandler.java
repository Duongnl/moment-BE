package com.moment.moment_BE.configuration;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
import java.security.Principal;
import java.util.Map;

public class UserHandshakeHandler  extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // Lấy userName từ attributes
        String userName = (String) attributes.get("userName");

        System.out.println("Determined user: " + userName);

        if (userName != null) {
            // Trả về UserPrincipal dựa trên userName
            return new UserPrincipal(userName);
        }

        return null; // Nếu không có userName, từ chối kết nối
    }
}
