package com.example.paperexchange.dtos;

import java.util.List;

public record PriceSubscriptionRequest(List<String> symbols) {
}
