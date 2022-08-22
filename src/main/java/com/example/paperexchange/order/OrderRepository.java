package com.example.paperexchange.order;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(long id);

    List<Order> findOrdersByUserEmail(String email);
}
