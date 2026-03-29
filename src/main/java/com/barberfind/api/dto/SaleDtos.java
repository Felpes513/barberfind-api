package com.barberfind.api.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SaleDtos {

    // ── Request ───────────────────────────────────────────────────

    public record CreateSaleRequest(

            String barberId, // opcional — OWNER pode informar; BARBER usa o próprio

            @NotEmpty(message = "items_required")
            @Valid
            List<SaleItemRequest> items
    ) {}

    public record SaleItemRequest(

            @NotBlank(message = "product_id_required")
            String productId,

            @NotNull(message = "quantity_required")
            @Min(value = 1, message = "quantity_must_be_at_least_1")
            Integer quantity
    ) {}

    // ── Response ──────────────────────────────────────────────────

    public record SaleResponse(
            String id,
            String barbershopId,
            String barbershopName,
            String barberId,
            String barberName,
            BigDecimal totalAmount,
            List<SaleItemResponse> items,
            LocalDateTime createdAt
    ) {}

    public record SaleItemResponse(
            String id,
            String productId,
            String productName,
            Integer quantity,
            BigDecimal unitPrice,
            BigDecimal subtotal
    ) {}
}