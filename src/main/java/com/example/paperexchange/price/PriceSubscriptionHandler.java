package com.example.paperexchange.price;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

@Component
public class PriceSubscriptionHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final PriceService priceService;

    @Autowired
    public PriceSubscriptionHandler(PriceService priceService) {
        this.priceService = priceService;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        try {
            PriceSubscription subscription = mapper.readValue(message.getPayload(), PriceSubscription.class);
            priceService.handleSubscriptions(subscription.symbols(), session);
        } catch (IOException e) {
        }
    }
}
