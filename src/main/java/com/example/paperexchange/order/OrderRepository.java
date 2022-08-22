package com.example.paperexchange.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Order findOrderById(long id);

    Page<Order> findOrdersByUserEmail(Pageable pageable, String email);
}
