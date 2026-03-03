package com.barberfind.api.controller;

import com.barberfind.api.dto.BarbershopBarberDtos.*;
import com.barberfind.api.service.BarbershopBarberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbershops/{barbershopId}/barbers")
@RequiredArgsConstructor
public class BarbershopBarberController {

    private final BarbershopBarberService barberService;

    // Autenticado — lista todos (ativos e pendentes)
    @GetMapping
    public ResponseEntity<List<BarbershopBarberResponse>> list(@PathVariable String barbershopId) {
        return ResponseEntity.ok(barberService.list(barbershopId));
    }

    // BARBER solicita entrada
    @PreAuthorize("hasRole('BARBER')")
    @PostMapping("/request")
    public ResponseEntity<LinkStatusResponse> request(@PathVariable String barbershopId) {
        return ResponseEntity.status(201).body(barberService.requestEntry(barbershopId));
    }

    // OWNER aprova
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{linkId}/approve")
    public ResponseEntity<LinkStatusResponse> approve(
            @PathVariable String barbershopId,
            @PathVariable String linkId) {
        return ResponseEntity.ok(barberService.approve(barbershopId, linkId));
    }

    // OWNER rejeita/desativa
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{linkId}/reject")
    public ResponseEntity<LinkStatusResponse> reject(
            @PathVariable String barbershopId,
            @PathVariable String linkId) {
        return ResponseEntity.ok(barberService.reject(barbershopId, linkId));
    }

    // OWNER remove ou BARBER sai
    @DeleteMapping("/{linkId}")
    public ResponseEntity<Map<String, String>> remove(
            @PathVariable String barbershopId,
            @PathVariable String linkId) {
        barberService.remove(barbershopId, linkId);
        return ResponseEntity.ok(Map.of("message", "removed"));
    }
}