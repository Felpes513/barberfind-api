package com.barberfind.api.repository;

import com.barberfind.api.domain.BarbershopPhoto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BarbershopPhotoRepository extends JpaRepository<BarbershopPhoto, String> {

    List<BarbershopPhoto> findAllByBarbershopId(String barbershopId);

    Optional<BarbershopPhoto> findByIdAndBarbershopId(String id, String barbershopId);
}