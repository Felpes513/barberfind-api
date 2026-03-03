package com.barberfind.api.controller;

import com.barberfind.api.dto.*;
import com.barberfind.api.service.BarbershopService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BarbershopController {

    private final BarbershopService service;

    public BarbershopController(BarbershopService service) {
        this.service = service;
    }

    // ===== PUBLIC =====

    @GetMapping("/barbershops")
    public ResponseEntity<List<BarbershopResponse>> listPublic() {
        return ResponseEntity.ok(service.listPublic());
    }

    @GetMapping("/barbershops/{id}")
    public ResponseEntity<BarbershopResponse> getPublic(@PathVariable String id) {
        return ResponseEntity.ok(service.getPublicById(id));
    }

    // ===== OWNER =====

    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/barbershops")
    public ResponseEntity<BarbershopResponse> create(@RequestBody BarbershopCreateRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/barbershops/{id}")
    public ResponseEntity<BarbershopResponse> update(
            @PathVariable String id,
            @RequestBody BarbershopUpdateRequest req
    ) {
        return ResponseEntity.ok(service.update(id, req));
    }

    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/barbershops/{id}/status")
    public ResponseEntity<BarbershopResponse> setStatus(
            @PathVariable String id,
            @RequestBody BarbershopStatusRequest req
    ) {
        return ResponseEntity.ok(service.setStatus(id, req));
    }

    @PreAuthorize("hasRole('OWNER')")
    @GetMapping("/owners/me/barbershops")
    public ResponseEntity<List<BarbershopResponse>> listMine() {
        return ResponseEntity.ok(service.listMine());
    }
}