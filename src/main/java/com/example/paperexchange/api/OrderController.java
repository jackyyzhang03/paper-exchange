package com.example.paperexchange.api;

import com.example.paperexchange.exception.NotAuthorizedException;
import com.example.paperexchange.exception.NotFoundException;
import com.example.paperexchange.order.Order;
import com.example.paperexchange.order.OrderDto;
import com.example.paperexchange.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        String email = authentication.getName();
        orderService.createOrder(orderDto, email);
    }

    @DeleteMapping("/{id}")
    public void cancelOrder(@PathVariable String id, Authentication authentication) {
        Order order = orderService.getOrder(Long.parseLong(id));
        if (order == null) throw new NotFoundException();
        if (order.getUser().getEmail().equals(authentication.getName())) {
            orderService.deleteOrder(order);
        } else {
            throw new NotAuthorizedException();
        }
    }

    @GetMapping
    public Page<OrderDto> getOrders(Pageable pageable, Authentication authentication) {
        Page<Order> orders = orderService.getOrders(pageable, authentication.getName());
        Page<OrderDto> page = orders.map((order) -> new OrderDto(order.getId(), order.getSymbol(), order.getType(), order.getShares(), order.getExecutionPrice(), order.getStopLimitPrice(), order.isSell()));
        return page;
    }
}
