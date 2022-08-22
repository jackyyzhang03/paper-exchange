package com.example.paperexchange.finnhub;

import com.example.paperexchange.price.PriceService;
import com.example.paperexchange.trade.TradeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class FinnhubWebSocketHandler extends TextWebSocketHandler {
    @Value("#{'${finnhub.symbols}'}")
    private String symbols;

    private static final Logger logger = LoggerFactory.getLogger(FinnhubWebSocketHandler.class);
    private final TradeService tradeService;
    private final PriceService priceService;
    private final ObjectMapper mapper = new ObjectMapper();

    private WebSocketSession session;

    @Autowired
    public FinnhubWebSocketHandler(TradeService tradeService, PriceService priceService) {
        this.tradeService = tradeService;
        this.priceService = priceService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        this.session = session;
        sendSubscriptions();
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("Connection closed with reason: {}", closeStatus.getReason());
        this.session = null;
        super.afterConnectionClosed(session, closeStatus);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        PriceDto priceDto = mapper.readValue(message.getPayload(), PriceDto.class);
        List<Price> prices = priceDto.data();
        if (prices == null) return;
        for (Price price : prices) {
            tradeService.processPriceUpdate(price);
            priceService.updatePrice(price);
        }
    }

    // @Scheduled(fixedDelay = 300)
    public void testPrice() {
        priceService.updatePrice(new Price("AAPL", Math.random() * 200));
        tradeService.processPriceUpdate(new Price("AAPL", Math.random() * 200));
        priceService.updatePrice(new Price("MSFT", Math.random() * 200));
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void sendSubscriptions() throws IOException {
        if (this.session == null) return;
        for (String symbol : symbols.split(",\\s")) {
            this.session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}"));
        }
    }
}
