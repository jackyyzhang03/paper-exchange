package com.example.paperexchange.finnhub;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class FinnhubUriBuilderFactory {
    @Value("${finnhub.token}")
    private String token;

    public UriBuilder getUriBuilder(String endpoint) {
        return UriComponentsBuilder.fromHttpUrl("https://finnhub.io/api/v1" + endpoint).queryParam("token", token);
    }
}
