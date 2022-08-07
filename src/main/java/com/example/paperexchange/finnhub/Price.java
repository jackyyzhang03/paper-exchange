package com.example.paperexchange.finnhub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Price(@JsonProperty("s") String symbol, @JsonProperty("p") double price) {
}
