package com.barberfind.api.repository;

import com.barberfind.api.domain.BarberAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BarberAvailabilityRepository extends JpaRepository<BarberAvailability, String> {
    List<BarberAvailability> findAllByBarberId(String barberId);
}