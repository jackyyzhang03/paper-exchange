package com.example.paperexchange.api;

import com.example.paperexchange.exception.NotAuthorizedException;
import com.example.paperexchange.exception.NotFoundException;
import com.example.paperexchange.order.Order;
import com.example.paperexchange.order.OrderDto;
import com.example.paperexchange.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public void createOrder(@RequestBody OrderDto orderDto, Authentication authentication) {
        String username = authentication.getName();
        orderService.createOrder(orderDto, username);
    }

    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable String id, Authentication authentication) {
        Order order = orderService.getOrder(Long.parseLong(id));
        if (order == null) throw new NotFoundException();
        if (order.getUser().getUsername().equals(authentication.getName())) {
            orderService.deleteOrder(order);
        } else {
            throw new NotAuthorizedException();
        }
    }

    @GetMapping
    public Map<String, List<OrderDto>> getOrders(Authentication authentication) {
        List<Order> orders = orderService.getOrders(authentication.getName());
        List<OrderDto> dtos = orders.stream().map((order) -> new OrderDto(order.getId(), order.getSymbol(), order.getType(), order.getShares(), order.getExecutionPrice(), order.getStopLimitPrice(), order.isSell())).toList();
        return Collections.singletonMap("orders", dtos);
    }
}
