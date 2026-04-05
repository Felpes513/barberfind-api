package com.barberfind.api.repository;

import com.barberfind.api.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<Sale, String> {

    List<Sale> findAllByBarbershopId(String barbershopId);

    Optional<Sale> findByIdAndBarbershopId(String id, String barbershopId);
}