package com.example.paperexchange.services;

import com.example.paperexchange.entities.Holding;
import com.example.paperexchange.entities.Order;
import com.example.paperexchange.entities.Trade;
import com.example.paperexchange.repositories.HoldingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PortfolioService {
    private final HoldingRepository holdingRepository;

    @Autowired
    public PortfolioService(HoldingRepository holdingRepository) {
        this.holdingRepository = holdingRepository;
    }

    public List<Holding> getUserHoldings(String email) {
        return holdingRepository.findHoldingsByUserEmail(email);
    }

    public Holding getHolding(String email, String symbol) {
        return holdingRepository.findHoldingByUserEmailAndSymbol(email, symbol);
    }

    public void processTrade(Trade trade) {
        Holding holding = holdingRepository.findHoldingByUserEmailAndSymbol(trade.getUser().getEmail(), trade.getSymbol());
        if (holding == null) holding = new Holding(trade.getSymbol(), 0, 0, trade.getUser());
        switch (trade.getType()) {
            case BUY -> {
                double bookValue = holding.getAdjustedCostBase() * holding.getShares();
                bookValue += trade.getPrice() * trade.getShares();
                holding.setShares(holding.getShares() + trade.getShares());
                holding.setAdjustedCostBase(bookValue / holding.getShares());
            }
            case SELL -> {
                holding.setShares(holding.getShares() - trade.getShares());
            }
        }
        if (holding.getShares() > 0) {
            holdingRepository.save(holding);
        } else {
            holdingRepository.delete(holding);
        }
    }

    public boolean checkValidOrder(Order order) {
        Holding holding = holdingRepository.findHoldingByUserEmailAndSymbol(order.getUser().getEmail(), order.getSymbol());
        return !order.isSell() || (holding != null && order.getShares() <= holding.getShares());
    }
}
