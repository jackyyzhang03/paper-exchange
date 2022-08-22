package com.example.paperexchange.trade;

import com.example.paperexchange.finnhub.Price;
import com.example.paperexchange.order.Order;
import com.example.paperexchange.order.OrderRepository;
import com.example.paperexchange.portfolio.PortfolioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Comparator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

@Service
@Transactional
public class TradeService {
    private final TradeRepository tradeRepository;
    private final OrderRepository orderRepository;
    private final PortfolioService portfolioService;

    private final Map<String, Queue<Order>> marketBuyOrders = new ConcurrentHashMap<>();
    private final Map<String, Queue<Order>> limitBuyOrders = new ConcurrentHashMap<>();
    private final Map<String, Queue<Order>> stopBuyOrders = new ConcurrentHashMap<>();

    private final Map<String, Queue<Order>> marketSellOrders = new ConcurrentHashMap<>();
    private final Map<String, Queue<Order>> limitSellOrders = new ConcurrentHashMap<>();
    private final Map<String, Queue<Order>> stopSellOrders = new ConcurrentHashMap<>();

    @Autowired
    public TradeService(TradeRepository tradeRepository, OrderRepository orderRepository, PortfolioService portfolioService) {
        this.tradeRepository = tradeRepository;
        this.orderRepository = orderRepository;
        this.portfolioService = portfolioService;
    }

    public void processPriceUpdate(Price price) {
        String symbol = price.symbol();
        // Process market orders
        Queue<Order> orders = marketBuyOrders.get(symbol);
        while (orders != null && !orders.isEmpty()) {
            executeOrder(orders.remove(), price.price());
        }
        orders = marketSellOrders.get(symbol);
        while (orders != null && !orders.isEmpty()) {
            executeOrder(orders.remove(), price.price());
        }

        // Process limit orders
        orders = limitBuyOrders.get(symbol);
        while (orders != null && !orders.isEmpty() && orders.peek().getExecutionPrice() >= price.price()) {
            executeOrder(orders.remove(), price.price());
        }
        orders = limitSellOrders.get(symbol);
        while (orders != null && !orders.isEmpty() && orders.peek().getExecutionPrice() <= price.price()) {
            executeOrder(orders.remove(), price.price());
        }

        // Process stop orders
        orders = stopBuyOrders.get(symbol);
        while (orders != null && !orders.isEmpty() && orders.peek().getExecutionPrice() <= price.price()) {
            executeOrder(orders.remove(), price.price());
        }
        orders = stopSellOrders.get(symbol);
        while (orders != null && !orders.isEmpty() && orders.peek().getExecutionPrice() >= price.price()) {
            executeOrder(orders.remove(), price.price());
        }
    }

    private void executeOrder(Order order, double price) {
        switch (order.getType()) {
            case STOP_LIMIT -> {
                // Convert to limit order
                order.setType(Order.Type.LIMIT);
                order.setExecutionPrice(order.getStopLimitPrice());
                if (order.isSell()) {
                    limitSellOrders.putIfAbsent(order.getSymbol(), createIncreasingQueue());
                    limitSellOrders.get(order.getSymbol()).add(order);
                } else {
                    limitBuyOrders.putIfAbsent(order.getSymbol(), createDecreasingQueue());
                    limitBuyOrders.get(order.getSymbol()).add(order);
                }
            }
            case STOP -> {
                // Convert to market order
                order.setType(Order.Type.MARKET);
                order.setExecutionPrice(0);
                if (order.isSell()) {
                    marketSellOrders.putIfAbsent(order.getSymbol(), createQueue());
                    marketSellOrders.get(order.getSymbol()).add(order);
                } else {
                    marketBuyOrders.putIfAbsent(order.getSymbol(), createQueue());
                    marketBuyOrders.get(order.getSymbol()).add(order);
                }
            }
            default -> {
                Trade trade = new Trade(order.isSell() ? Trade.Type.SELL : Trade.Type.BUY, order.getSymbol(), price, order.getShares(), Instant.now(), order.getUser());
                tradeRepository.save(trade);
                portfolioService.processTrade(trade);
                orderRepository.delete(order);
            }
        }
    }

    public void addOrder(Order order) {
        String symbol = order.getSymbol();
        Order.Type type = order.getType();
        switch (type) {
            case STOP, STOP_LIMIT -> {
                if (order.isSell()) {
                    // Stop sell orders are executed from highest to lowest price
                    stopSellOrders.putIfAbsent(symbol, createDecreasingQueue());
                    stopSellOrders.get(symbol).add(order);
                } else {
                    // Stop buy orders are executed from lowest to greatest price
                    stopBuyOrders.putIfAbsent(symbol, createIncreasingQueue());
                    stopBuyOrders.get(symbol).add(order);
                }
            }
            case MARKET -> {
                // Market orders are ordered FIFO
                if (order.isSell()) {
                    marketSellOrders.putIfAbsent(symbol, createQueue());
                    marketSellOrders.get(symbol).add(order);
                } else {
                    marketBuyOrders.putIfAbsent(symbol, createQueue());
                    marketBuyOrders.get(symbol).add(order);
                }
            }
            case LIMIT -> {
                if (order.isSell()) {
                    // Limit sell orders are executed from lowest to highest price
                    limitSellOrders.putIfAbsent(symbol, createIncreasingQueue());
                    limitSellOrders.get(symbol).add(order);
                } else {
                    // Limit buy orders are executed from highest to lowest price
                    limitBuyOrders.putIfAbsent(symbol, createDecreasingQueue());
                    limitBuyOrders.get(symbol).add(order);
                }
            }
        }
    }

    private Queue<Order> createQueue() {
        return new ArrayBlockingQueue<>(16);
    }

    private Queue<Order> createDecreasingQueue() {
        return new PriorityBlockingQueue<>(16, Comparator.comparingDouble(Order::getExecutionPrice).reversed());
    }

    private Queue<Order> createIncreasingQueue() {
        return new PriorityBlockingQueue<>(16, Comparator.comparingDouble(Order::getExecutionPrice));
    }

    @PostConstruct
    public void loadOrdersFromDataSource() {
        orderRepository.findAll().forEach(this::addOrder);
    }
}
