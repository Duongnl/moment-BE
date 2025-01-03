package com.moment.moment_BE.configuration;

import jakarta.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {


    private final HttpHandshakeInterceptor httpHandshakeInterceptor;

    @Value("${allowed}")
    private String allowedOrigin;

    @Autowired
    public WebSocketConfig(HttpHandshakeInterceptor httpHandshakeInterceptor) {
        this.httpHandshakeInterceptor = httpHandshakeInterceptor;
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns(allowedOrigin)
                .setHandshakeHandler(new UserHandshakeHandler())
                .addInterceptors(httpHandshakeInterceptor)
                .withSockJS();

    }



}
