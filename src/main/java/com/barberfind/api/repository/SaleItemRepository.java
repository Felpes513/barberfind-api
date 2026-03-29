package com.barberfind.api.repository;

import com.barberfind.api.domain.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleItemRepository extends JpaRepository<SaleItem, String> {
}