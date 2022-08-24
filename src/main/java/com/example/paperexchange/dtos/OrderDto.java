package com.example.paperexchange.dtos;

import com.example.paperexchange.entities.Order;

public record OrderDto(long id, String symbol, Order.Type type, int shares, double executionPrice,
                       double stopLimitPrice, boolean sell) {
}
