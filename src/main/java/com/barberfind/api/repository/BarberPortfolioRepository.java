package com.barberfind.api.repository;

import com.barberfind.api.domain.BarberPortfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BarberPortfolioRepository extends JpaRepository<BarberPortfolio, String> {
    List<BarberPortfolio> findAllByBarberId(String barberId);
}