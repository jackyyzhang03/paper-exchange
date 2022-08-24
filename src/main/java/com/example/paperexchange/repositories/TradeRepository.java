package com.example.paperexchange.repositories;

import com.example.paperexchange.entities.Trade;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    Page<Trade> findTradesByUserEmail(Pageable pageable, String email);
}