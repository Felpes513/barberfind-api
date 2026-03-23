package com.barberfind.api.controller;

import com.barberfind.api.dto.ProductDtos.*;
import com.barberfind.api.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/barbershops/{barbershopId}/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // GET /api/barbershops/{barbershopId}/products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> list(
            @PathVariable String barbershopId
    ) {
        return ResponseEntity.ok(productService.listByBarbershop(barbershopId));
    }

    // GET /api/barbershops/{barbershopId}/products/{productId}
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getById(
            @PathVariable String barbershopId,
            @PathVariable String productId
    ) {
        return ResponseEntity.ok(productService.getById(barbershopId, productId));
    }

    // POST /api/barbershops/{barbershopId}/products
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ProductResponse> create(
            @PathVariable String barbershopId,
            @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.create(barbershopId, req));
    }

    // PUT /api/barbershops/{barbershopId}/products/{productId}
    @PutMapping("/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<ProductResponse> update(
            @PathVariable String barbershopId,
            @PathVariable String productId,
            @Valid @RequestBody ProductRequest req
    ) {
        return ResponseEntity.ok(productService.update(barbershopId, productId, req));
    }

    // DELETE /api/barbershops/{barbershopId}/products/{productId}
    @DeleteMapping("/{productId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(
            @PathVariable String barbershopId,
            @PathVariable String productId
    ) {
        productService.delete(barbershopId, productId);
        return ResponseEntity.noContent().build();
    }
}