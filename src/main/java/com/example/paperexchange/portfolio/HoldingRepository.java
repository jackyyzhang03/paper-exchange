package com.example.paperexchange.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    public List<Holding> findHoldingsByUserUsername(String username);
    public Holding findHoldingByUserUsernameAndSymbol(String username, String symbol);
}