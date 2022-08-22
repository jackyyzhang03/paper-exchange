package com.example.paperexchange.api;

import com.example.paperexchange.trade.Trade;
import com.example.paperexchange.trade.TradeDto;
import com.example.paperexchange.trade.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/trades")
public class TradeController {
    private final TradeRepository tradeRepository;

    @Autowired
    public TradeController(TradeRepository tradeRepository) {
        this.tradeRepository = tradeRepository;
    }

    @GetMapping
    public Page<TradeDto> getTrades(Pageable pageable, Authentication authentication) {
        Page<Trade> trades = tradeRepository.findTradesByUserEmail(pageable, authentication.getName());
        Page<TradeDto> page = trades.map((trade) -> new TradeDto(trade.getType(), trade.getSymbol(), trade.getPrice(), trade.getShares(), trade.getTime()));
        return page;
    }
}
