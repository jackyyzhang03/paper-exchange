package com.example.paperexchange.api;

import com.example.paperexchange.portfolio.Holding;
import com.example.paperexchange.portfolio.HoldingDto;
import com.example.paperexchange.portfolio.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public Page<HoldingDto> getPortfolio(Pageable pageable, Authentication authentication) {
        Page<Holding> holdings = portfolioService.getUserHoldings(authentication.getName(), pageable);
        Page<HoldingDto> page = holdings.map(holding -> new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase()));
        return page;
    }

    @GetMapping("/holdings/{symbol}")
    public HoldingDto getHolding(@PathVariable String symbol, Authentication authentication) {
        Holding holding = portfolioService.getHolding(authentication.getName(), symbol);
        if (holding == null) return new HoldingDto(symbol, 0, 0);
        return new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase());
    }
}
