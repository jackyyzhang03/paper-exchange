package com.example.paperexchange.order;

import com.example.paperexchange.exception.InsufficientSharesException;
import com.example.paperexchange.portfolio.PortfolioService;
import com.example.paperexchange.trade.TradeService;
import com.example.paperexchange.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final TradeService tradeService;
    private final PortfolioService portfolioService;

    @Autowired
    public OrderService(OrderRepository orderRepository, UserRepository userRepository, TradeService tradeService, PortfolioService portfolioService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.tradeService = tradeService;
        this.portfolioService = portfolioService;
    }

    public List<Order> getOrders(String email) {
        return orderRepository.findOrdersByUserEmail(email);
    }

    public Order getOrder(long id) {
        return orderRepository.findOrderById(id);
    }

    public void createOrder(OrderDto orderDto, String email) {
        Order order = new Order();
        order.setType(orderDto.type());
        order.setSymbol(orderDto.symbol());
        order.setShares(orderDto.shares());
        order.setExecutionPrice(orderDto.executionPrice());
        order.setStopLimitPrice(orderDto.stopLimitPrice());
        order.setSell(orderDto.sell());
        order.setUser(userRepository.findUserByEmail(email));
        if (portfolioService.checkValidOrder(order)) {
            orderRepository.save(order);
            tradeService.addOrder(order);
        } else {
            throw new InsufficientSharesException();
        }
    }

    public void deleteOrder(Order order) {
        orderRepository.delete(order);
    }
}
