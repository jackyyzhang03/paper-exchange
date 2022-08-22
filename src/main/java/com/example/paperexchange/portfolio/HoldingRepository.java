package com.example.paperexchange.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    List<Holding> findHoldingsByUserEmail(String email);

    Holding findHoldingByUserEmailAndSymbol(String email, String symbol);
}