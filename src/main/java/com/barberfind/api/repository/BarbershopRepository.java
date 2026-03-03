package com.barberfind.api.repository;

import com.barberfind.api.domain.Barbershop;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface BarbershopRepository extends JpaRepository<Barbershop, String> {
    List<Barbershop> findByIsActiveTrue();
    List<Barbershop> findByOwnerUserId(String ownerUserId);
    Optional<Barbershop> findByIdAndOwnerUserId(String id, String ownerUserId);
}