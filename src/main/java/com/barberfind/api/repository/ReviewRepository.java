package com.barberfind.api.repository;

import com.barberfind.api.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, String> {

    List<Review> findAllByBarbershopId(String barbershopId);

    List<Review> findAllByBarber_Id(String barberId);

    // Garante um review por cliente por barbearia
    boolean existsByUser_IdAndBarbershopId(String userId, String barbershopId);

    // Garante um review por cliente por barbeiro
    boolean existsByUser_IdAndBarber_Id(String userId, String barberId);

    Optional<Review> findByIdAndBarbershopId(String id, String barbershopId);
}