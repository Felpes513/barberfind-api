package com.barberfind.api.repository;

import com.barberfind.api.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, String> {

    List<Product> findAllByBarbershopId(String barbershopId);

    boolean existsByBarbershopIdAndName(String barbershopId, String name);
}