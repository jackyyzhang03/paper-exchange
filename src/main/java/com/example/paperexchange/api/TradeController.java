package com.example.paperexchange.api;

import com.example.paperexchange.trade.Trade;
import com.example.paperexchange.trade.TradeDto;
import com.example.paperexchange.trade.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private final TradeRepository tradeRepository;

    @Autowired
    public TradeController(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @GetMapping
    public Map<String, List<TradeDto>> getTrades(Authentication authentication) {
        List<Trade> trades = tradeRepository.findTradesByUserEmail(authentication.getName());
        List<TradeDto> dtos = trades.stream().map((trade) -> new TradeDto(trade.getType(), trade.getSymbol(), trade.getPrice(), trade.getShares(), trade.getTime())).toList();
        return Collections.singletonMap("trades", dtos);
    }
}
