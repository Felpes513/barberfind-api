package com.barberfind.api.controller;

import com.barberfind.api.dto.ServiceDtos.*;
import com.barberfind.api.service.GlobalServiceManager;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ServiceController {

    private final GlobalServiceManager manager;

    // ── Serviços globais ──────────────────────────────────────────────────────

    // OWNER cria serviço global
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/api/services")
    public ResponseEntity<ServiceResponse> createGlobal(@Valid @RequestBody ServiceCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manager.createGlobal(req));
    }

    // Público — qualquer um lista os serviços disponíveis
    @GetMapping("/api/services")
    public ResponseEntity<List<ServiceResponse>> listGlobal() {
        return ResponseEntity.ok(manager.listGlobal());
    }

    // ── Vínculos barbearia ↔ serviço ──────────────────────────────────────────

    // Público — lista serviços de uma barbearia
    @GetMapping("/api/barbershops/{barbershopId}/services")
    public ResponseEntity<List<BarbershopServiceResponse>> listByBarbershop(
            @PathVariable String barbershopId) {
        return ResponseEntity.ok(manager.listByBarbershop(barbershopId));
    }

    // OWNER vincula serviço à barbearia
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/api/barbershops/{barbershopId}/services")
    public ResponseEntity<BarbershopServiceResponse> link(
            @PathVariable String barbershopId,
            @Valid @RequestBody BarbershopServiceLinkRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(manager.link(barbershopId, req));
    }

    // OWNER atualiza preço customizado do vínculo
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/api/barbershops/{barbershopId}/services/{linkId}")
    public ResponseEntity<BarbershopServiceResponse> updateLink(
            @PathVariable String barbershopId,
            @PathVariable String linkId,
            @RequestBody BarbershopServiceUpdateRequest req) {
        return ResponseEntity.ok(manager.updateLink(barbershopId, linkId, req));
    }

    // OWNER desvincula serviço da barbearia
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/api/barbershops/{barbershopId}/services/{linkId}")
    public ResponseEntity<Map<String, String>> unlink(
            @PathVariable String barbershopId,
            @PathVariable String linkId) {
        manager.unlink(barbershopId, linkId);
        return ResponseEntity.ok(Map.of("message", "unlinked"));
    }
}