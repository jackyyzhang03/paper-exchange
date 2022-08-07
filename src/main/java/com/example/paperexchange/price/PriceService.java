package com.example.paperexchange.price;

import com.example.paperexchange.exception.NotFoundException;
import com.example.paperexchange.finnhub.Price;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PriceService {
    private final Map<String, Set<WeakReference<WebSocketSession>>> connectionMap = new ConcurrentHashMap<>();
    private final Map<String, PriceUpdate> priceMap = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate restTemplate;

    @Autowired
    public PriceService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public void handleSubscriptions(List<String> symbols, WebSocketSession session) throws IOException {
        for (String symbol : symbols) {
            connectionMap.putIfAbsent(symbol, ConcurrentHashMap.newKeySet());
            connectionMap.get(symbol).add(new WeakReference<>(session));
            publishPrice(session, new Price(symbol, getPrice(symbol)));
        }
    }

    public double getPrice(String symbol) {
        if (priceMap.containsKey(symbol)) {
            return priceMap.get(symbol).getPrice();
        }
        URI uri = UriComponentsBuilder.fromHttpUrl("https://finnhub.io/api/v1/quote")
                .queryParam("symbol", symbol)
                .queryParam("token", "***REMOVED***")
                .build()
                .toUri();
        Quote quote = this.restTemplate.getForObject(uri, Quote.class);
        if (quote == null) throw new NotFoundException();
        return quote.currentPrice();
    }

    public void updatePrice(Price price) {
        priceMap.put(price.symbol(), new PriceUpdate(price.price()));
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
                publishPrice(session, new Price(symbol, price));
            } else {
                sessions.remove(sessionWeakReference);
            }
        }
    }

    private void publishPrice(WebSocketSession session, Price price) throws IOException {
        synchronized (session) {
            session.sendMessage(new TextMessage(mapper.writeValueAsString(price)));
        }
    }
}
