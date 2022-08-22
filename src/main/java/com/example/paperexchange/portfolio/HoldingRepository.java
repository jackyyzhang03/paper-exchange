package com.example.paperexchange.portfolio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoldingRepository extends JpaRepository<Holding, Long> {
    Page<Holding> findHoldingsByUserEmail(String email, Pageable pageable);

    Holding findHoldingByUserEmailAndSymbol(String email, String symbol);
}