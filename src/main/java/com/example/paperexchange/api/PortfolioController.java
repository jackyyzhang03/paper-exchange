package com.example.paperexchange.api;

import com.example.paperexchange.portfolio.Holding;
import com.example.paperexchange.portfolio.HoldingDto;
import com.example.paperexchange.portfolio.PortfolioDto;
import com.example.paperexchange.portfolio.PortfolioService;
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
    public PortfolioDto getPortfolio(Authentication authentication) {
        List<Holding> holdings = portfolioService.getUserHoldings(authentication.getName());
        List<HoldingDto> dtos = holdings.stream()
                .map(holding -> new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase()))
                .toList();
        return new PortfolioDto(dtos);
    }

    @GetMapping("/holdings/{symbol}")
    public HoldingDto getHolding(Authentication authentication, @PathVariable String symbol) {
        Holding holding = portfolioService.getHolding(authentication.getName(), symbol);
        if (holding == null) return new HoldingDto(symbol, 0, 0);
        return new HoldingDto(holding.getSymbol(), holding.getShares(), holding.getAdjustedCostBase());
    }
}
