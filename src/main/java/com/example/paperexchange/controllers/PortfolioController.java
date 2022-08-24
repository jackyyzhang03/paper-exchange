package com.example.paperexchange.controllers;

import com.example.paperexchange.dtos.HoldingDto;
import com.example.paperexchange.entities.Holding;
import com.example.paperexchange.services.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/portfolio")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @Autowired
    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @GetMapping
    public List<HoldingDto> getPortfolio(Authentication authentication) {
        List<Holding> entities = portfolioService.getUserHoldings(authentication.getName());
        List<HoldingDto> holdings = entities.stream().map(holding -> new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase())).toList();
        return holdings;
    }

    @GetMapping("/holdings/{symbol}")
    public HoldingDto getHolding(@PathVariable String symbol, Authentication authentication) {
        Holding holding = portfolioService.getHolding(authentication.getName(), symbol);
        if (holding == null) return new HoldingDto(symbol, 0, 0);
        return new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase());
    }
}
