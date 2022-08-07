package com.example.paperexchange.finnhub;

import java.util.List;

public record PriceDto(String type, List<Price> data) {
}
