package com.barberfind.api.repository;

import com.barberfind.api.domain.BarbershopBarber;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarbershopBarberRepository extends JpaRepository<BarbershopBarber, String> {

    List<BarbershopBarber> findAllByBarbershopId(String barbershopId);

    Optional<BarbershopBarber> findByBarbershopIdAndBarberId(String barbershopId, String barberId);

    boolean existsByBarbershopIdAndBarberId(String barbershopId, String barberId);
}