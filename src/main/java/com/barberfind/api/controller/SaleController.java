package com.barberfind.api.controller;

import com.barberfind.api.dto.SaleDtos.*;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbershops/{barbershopId}/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    // GET /api/barbershops/{barbershopId}/sales
    @GetMapping
    @PreAuthorize("hasAnyRole('OWNER', 'BARBER')")
    public ResponseEntity<List<SaleResponse>> list(@PathVariable String barbershopId) {
        return ResponseEntity.ok(
                saleService.listByBarbershop(barbershopId, AuthUtils.getUserId(), AuthUtils.getRole())
        );
    }

    // GET /api/barbershops/{barbershopId}/sales/{saleId}
    @GetMapping("/{saleId}")
    @PreAuthorize("hasAnyRole('OWNER', 'BARBER')")
    public ResponseEntity<SaleResponse> getById(
            @PathVariable String barbershopId,
            @PathVariable String saleId
    ) {
        return ResponseEntity.ok(
                saleService.getById(barbershopId, saleId, AuthUtils.getUserId(), AuthUtils.getRole())
        );
    }

    // POST /api/barbershops/{barbershopId}/sales
    @PostMapping
    @PreAuthorize("hasAnyRole('OWNER', 'BARBER')")
    public ResponseEntity<SaleResponse> create(
            @PathVariable String barbershopId,
            @Valid @RequestBody CreateSaleRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                saleService.create(barbershopId, req, AuthUtils.getUserId(), AuthUtils.getRole())
        );
    }

    // DELETE /api/barbershops/{barbershopId}/sales/{saleId}
    @DeleteMapping("/{saleId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(
            @PathVariable String barbershopId,
            @PathVariable String saleId
    ) {
        saleService.delete(barbershopId, saleId, AuthUtils.getUserId());
        return ResponseEntity.noContent().build();
    }
}