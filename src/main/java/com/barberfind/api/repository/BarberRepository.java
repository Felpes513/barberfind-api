package com.barberfind.api.repository;

import com.barberfind.api.domain.BarberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BarberRepository extends JpaRepository<BarberEntity, String> {

    Optional<BarberEntity> findByUserId(String userId);

    boolean existsByUserId(String userId);
}