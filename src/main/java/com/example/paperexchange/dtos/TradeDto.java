package com.example.paperexchange.dtos;

import com.example.paperexchange.entities.Trade;

import java.time.Instant;

public record TradeDto(Trade.Type type, String symbol, double price, int shares, Instant time) {
}
