package com.barberfind.api.repository;

import com.barberfind.api.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Service, String> {
    boolean existsByName(String name);
}