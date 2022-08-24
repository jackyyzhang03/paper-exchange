package com.example.paperexchange.services;

import com.example.paperexchange.dtos.CandlestickChart;
import com.example.paperexchange.dtos.PriceDto;
import com.example.paperexchange.dtos.QuoteDto;
import com.example.paperexchange.exception.ApiException;
import com.example.paperexchange.exception.NotFoundException;
import com.example.paperexchange.utility.FinnhubUriBuilderFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceService {
    private static final Logger logger = LoggerFactory.getLogger(PriceService.class);
    private final Map<String, Set<WeakReference<WebSocketSession>>> connectionMap = new ConcurrentHashMap<>();
    private final Map<String, PriceUpdate> priceMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;
    private final FinnhubUriBuilderFactory finnhubUriBuilderFactory;

    @Autowired
    public PriceService(RestTemplateBuilder restTemplateBuilder, FinnhubUriBuilderFactory finnhubUriBuilderFactory) {
        this.restTemplate = restTemplateBuilder.build();
        this.finnhubUriBuilderFactory = finnhubUriBuilderFactory;
    }

    public void handleSubscriptions(List<String> symbols, WebSocketSession session) throws IOException {
        for (String symbol : symbols) {
            connectionMap.putIfAbsent(symbol, ConcurrentHashMap.newKeySet());
            connectionMap.get(symbol).add(new WeakReference<>(session));
            publishPrice(session, new PriceDto(symbol, getPrice(symbol)));
        }
    }

    public double getPrice(String symbol) {
        if (priceMap.containsKey(symbol)) {
            return priceMap.get(symbol).getPrice();
        }
        URI uri = finnhubUriBuilderFactory.getUriBuilder("/quote").queryParam("symbol", symbol).build();
        QuoteDto quoteDto = restTemplate.getForObject(uri, QuoteDto.class);
        if (quoteDto == null) throw new NotFoundException();
        return quoteDto.currentPrice();
    }

    public CandlestickChart getCandles(String symbol, String resolution, long from, long to) {
        URI uri = finnhubUriBuilderFactory.getUriBuilder("/stock/candle")
                .queryParam("symbol", symbol)
                .queryParam("resolution", resolution)
                .queryParam("from", from)
                .queryParam("to", to)
                .build();

        try {
            return restTemplate.getForObject(uri, CandlestickChart.class);
        } catch (RestClientException e) {
            logger.error("Error fetching candles from API: {}", e.getMessage());
            throw new ApiException();
        }
    }

    public void updatePrice(PriceDto priceDto) {
        priceMap.put(priceDto.symbol(), new PriceUpdate(priceDto.price()));
    }

    @Scheduled(fixedDelay = 1000)
    private void publishPrices() throws IOException {
        for (Map.Entry<String, PriceUpdate> entry : priceMap.entrySet()) {
            PriceUpdate update = entry.getValue();
            String symbol = entry.getKey();
            if (!update.isPublished()) {
                publishUpdate(symbol, update.getPrice());
                update.publish();
            }
        }
    }

    private void publishUpdate(String symbol, double price) throws IOException {
        connectionMap.putIfAbsent(symbol, ConcurrentHashMap.newKeySet());
        Set<WeakReference<WebSocketSession>> sessions = connectionMap.get(symbol);
        for (WeakReference<WebSocketSession> sessionWeakReference : sessions) {
            WebSocketSession session = sessionWeakReference.get();
            if (session != null && session.isOpen()) {
                publishPrice(session, new PriceDto(symbol, price));
            } else {
                sessions.remove(sessionWeakReference);
            }
        }
    }

    private void publishPrice(WebSocketSession session, PriceDto priceDto) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(priceDto)));
        }
    }

    private class PriceUpdate {
        private final double price;
        private volatile boolean published = false;

        public PriceUpdate(double price) {
            this.price = price;
        }

        public double getPrice() {
            return price;
        }

        public boolean isPublished() {
            return published;
        }

        public void publish() {
            this.published = true;
        }
    }
}
