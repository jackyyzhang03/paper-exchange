package com.example.paperexchange.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record QuoteDto(@JsonProperty("c") double currentPrice) {
}
