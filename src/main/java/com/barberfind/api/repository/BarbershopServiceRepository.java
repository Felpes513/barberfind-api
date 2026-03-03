package com.barberfind.api.repository;

import com.barberfind.api.domain.BarbershopService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarbershopServiceRepository extends JpaRepository<BarbershopService, String> {

    List<BarbershopService> findAllByBarbershopId(String barbershopId);

    Optional<BarbershopService> findByIdAndBarbershopId(String id, String barbershopId);

    boolean existsByBarbershopIdAndServiceId(String barbershopId, String serviceId);
}