package com.barberfind.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDtos {

    public record ProductRequest(
            @NotBlank(message = "name_required")
            String name,

            @NotNull(message = "price_required")
            @DecimalMin(value = "0.0", inclusive = false, message = "price_must_be_positive")
            BigDecimal price,

            @NotNull(message = "stock_quantity_required")
            @Min(value = 0, message = "stock_quantity_must_be_non_negative")
            Integer stockQuantity
    ) {}

    public record ProductResponse(
            String id,
            String barbershopId,
            String barbershopName,
            String name,
            BigDecimal price,
            Integer stockQuantity,
            LocalDateTime createdAt
    ) {}
}