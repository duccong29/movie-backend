package movies.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register STOMP endpoints for WebSocket connections
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Enable a simple in-memory message broker
        // Messages whose destination starts with "/app" should be routed to message-handling methods
        registry.setApplicationDestinationPrefixes("/app");

        // Messages whose destination starts with "/topic" or "/queue" should be routed to the message broker
        // "/topic" for one-to-many (broadcasting)
        // "/queue" for one-to-one (private messages)
        registry.enableSimpleBroker("/topic", "/queue");

        // Set the prefix for user-specific destinations
        registry.setUserDestinationPrefix("/user");
    }
}
