package com.chatApi.chatapi.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // websocket entrypoint (clients connect here)
        registry.addEndpoint("/ws-chat").setAllowedOriginPatterns("*").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // messages sent to destinations prefixed with /app will be routed to @MessageMapping
        config.setApplicationDestinationPrefixes("/app");
        // enable simple broker to broadcast messages to subscribers
        config.enableSimpleBroker("/topic");
    }
}
