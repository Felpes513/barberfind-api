package com.barberfind.api.repository;

import com.barberfind.api.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, String> {

    List<Favorite> findAllByUserId(String userId);

    Optional<Favorite> findByUserIdAndBarberId(String userId, String barberId);

    Optional<Favorite> findByUserIdAndBarbershopId(String userId, String barbershopId);

    boolean existsByUserIdAndBarberId(String userId, String barberId);

    boolean existsByUserIdAndBarbershopId(String userId, String barbershopId);
}