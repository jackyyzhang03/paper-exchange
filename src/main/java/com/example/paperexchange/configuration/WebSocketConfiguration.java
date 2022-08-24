package com.example.paperexchange.configuration;

import com.example.paperexchange.websocket.PriceSubscriptionWebSocketHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final PriceSubscriptionWebSocketHandler priceSubscriptionWebSocketHandler;

    @Autowired
    public WebSocketConfiguration(PriceSubscriptionWebSocketHandler priceSubscriptionWebSocketHandler) {
        this.priceSubscriptionWebSocketHandler = priceSubscriptionWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(priceSubscriptionWebSocketHandler, "/prices").setAllowedOrigins("http://localhost:4200");
    }
}
