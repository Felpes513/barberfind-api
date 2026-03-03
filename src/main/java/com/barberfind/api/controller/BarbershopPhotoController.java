package com.barberfind.api.controller;

import com.barberfind.api.dto.ServiceDtos.*;
import com.barberfind.api.service.BarbershopPhotoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/barbershops/{barbershopId}/photos")
@RequiredArgsConstructor
public class BarbershopPhotoController {

    private final BarbershopPhotoService photoService;

    // Público — lista fotos de uma barbearia
    @GetMapping
    public ResponseEntity<List<PhotoResponse>> list(@PathVariable String barbershopId) {
        return ResponseEntity.ok(photoService.list(barbershopId));
    }

    // OWNER adiciona foto
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<PhotoResponse> add(
            @PathVariable String barbershopId,
            @Valid @RequestBody PhotoAddRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(photoService.add(barbershopId, req));
    }

    // OWNER remove foto
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{photoId}")
    public ResponseEntity<Map<String, String>> delete(
            @PathVariable String barbershopId,
            @PathVariable String photoId) {
        photoService.delete(barbershopId, photoId);
        return ResponseEntity.ok(Map.of("message", "deleted"));
    }
}