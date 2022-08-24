package com.example.paperexchange.websocket;

import com.example.paperexchange.dtos.PriceSubscriptionRequest;
import com.example.paperexchange.services.PriceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class PriceSubscriptionWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final PriceService priceService;

    @Autowired
    public PriceSubscriptionWebSocketHandler(PriceService priceService) {
        this.priceService = priceService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            PriceSubscriptionRequest subscription = mapper.readValue(message.getPayload(), PriceSubscriptionRequest.class);
            priceService.handleSubscriptions(subscription.symbols(), session);
        } catch (IOException e) {
        }
    }
}
