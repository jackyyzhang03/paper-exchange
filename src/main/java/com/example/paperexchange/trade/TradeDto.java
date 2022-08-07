package com.example.paperexchange.trade;

import java.time.Instant;

public record TradeDto(Trade.Type type, String symbol, double price, int shares, Instant time) {
}
