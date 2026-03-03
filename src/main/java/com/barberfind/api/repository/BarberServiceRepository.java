package com.barberfind.api.repository;

import com.barberfind.api.domain.BarberService;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BarberServiceRepository extends JpaRepository<BarberService, String> {
    List<BarberService> findAllByBarberId(String barberId);
}