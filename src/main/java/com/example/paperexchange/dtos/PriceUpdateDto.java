package com.example.paperexchange.dtos;

import java.util.List;

public record PriceUpdateDto(String type, List<PriceDto> data) {
}
