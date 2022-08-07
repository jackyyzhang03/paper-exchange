package com.example.paperexchange.price;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfiguration implements WebSocketConfigurer {
    private final PriceSubscriptionHandler priceSubscriptionHandler;

    @Autowired
    public WebSocketConfiguration(PriceSubscriptionHandler priceSubscriptionHandler) {
        this.priceSubscriptionHandler = priceSubscriptionHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(priceSubscriptionHandler, "/prices").setAllowedOrigins("http://localhost:4200");
    }
}
