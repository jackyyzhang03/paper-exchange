package com.example.paperexchange.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public Order findOrderById(long id);
    public List<Order> findOrdersByUserUsername(String username);
}
