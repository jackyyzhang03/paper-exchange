package com.example.paperexchange.controllers;

import com.example.paperexchange.dtos.CandlestickChart;
import com.example.paperexchange.exception.InvalidRequestException;
import com.example.paperexchange.services.PriceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/candles")
public class CandlestickController {
    private final PriceService priceService;

    @Autowired
    public CandlestickController(PriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/{symbol}")
    public CandlestickChart getCandlesticks(@PathVariable String symbol, @Param("number") int number, @Param("resolution") String resolution, @Param("unit") String unit) {
        LocalDate now = LocalDate.now();
        LocalDate date = switch (unit) {
            case "day" -> now.minusDays(number);
            case "week" -> now.minusWeeks(number);
            case "month" -> now.minusMonths(number);
            case "year" -> now.minusYears(number);
            case "all" -> LocalDate.ofEpochDay(0);
            default -> throw new InvalidRequestException();
        };
        long from = date.plusDays(1).atStartOfDay().atZone(ZoneId.systemDefault()).toEpochSecond();
        long to = Instant.now().getEpochSecond();
        return priceService.getCandles(symbol, resolution, from, to);
    }
}
