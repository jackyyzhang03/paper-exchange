package com.example.paperexchange.finnhub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.WebSocketConnectionManager;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;

@Configuration
public class WebSocketClientConfiguration {
    @Autowired
    public FinnhubWebSocketHandler webSocketHandler;

    @Bean
    public WebSocketClient webSocketClient() {
        return new StandardWebSocketClient();
    }

    @Bean
    public WebSocketConnectionManager webSocketConnectionManager() {
        WebSocketConnectionManager manager = new WebSocketConnectionManager(
                webSocketClient(),
                webSocketHandler,
                "wss://ws.finnhub.io?token=***REMOVED***"
        );
        manager.setAutoStartup(true);
        return manager;
    }
}
