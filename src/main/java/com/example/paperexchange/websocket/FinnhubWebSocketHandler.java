package com.example.paperexchange.websocket;

import com.example.paperexchange.dtos.PriceDto;
import com.example.paperexchange.dtos.PriceUpdateDto;
import com.example.paperexchange.services.PriceService;
import com.example.paperexchange.services.TradeService;
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
    private static final Logger logger = LoggerFactory.getLogger(FinnhubWebSocketHandler.class);
    private final TradeService tradeService;
    private final PriceService priceService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Value("#{'${finnhub.symbols}'}")
    private String symbols;
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
        PriceUpdateDto priceUpdateDto = mapper.readValue(message.getPayload(), PriceUpdateDto.class);
        List<PriceDto> priceDtos = priceUpdateDto.data();
        if (priceDtos == null) return;
        for (PriceDto priceDto : priceDtos) {
            tradeService.processPriceUpdate(priceDto);
            priceService.updatePrice(priceDto);
        }
    }

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.MINUTES)
    private void sendSubscriptions() throws IOException {
        if (this.session == null) return;
        for (String symbol : symbols.split(",\\s")) {
            this.session.sendMessage(new TextMessage("{\"type\":\"subscribe\",\"symbol\":\"" + symbol + "\"}"));
        }
    }
}
