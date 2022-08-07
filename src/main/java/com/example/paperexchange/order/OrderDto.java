package com.example.paperexchange.order;

public record OrderDto(long id, String symbol, Order.Type type, int shares, double executionPrice, double stopLimitPrice, boolean sell) {
}
