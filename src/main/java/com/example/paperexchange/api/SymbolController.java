package com.example.paperexchange.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/symbols")
public class SymbolController {
    @Value("#{'${finnhub.symbols}'}")
    private String symbols;

    @GetMapping
    public Map<String, List<String>> getSymbols() {
        return Collections.singletonMap("symbols", Arrays.asList(symbols.split(",\\s")));
    }
}
