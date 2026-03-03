package com.barberfind.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ServiceDtos {

    // ── Serviço Global ────────────────────────────────────────────────────────

    public record ServiceCreateRequest(
            @NotBlank(message = "name_required")
            String name,

            String description,

            @NotNull(message = "base_price_required")
            @DecimalMin(value = "0.0", inclusive = false, message = "base_price_invalid")
            BigDecimal basePrice,

            @NotNull(message = "duration_required")
            @Min(value = 1, message = "duration_invalid")
            Integer durationMinutes
    ) {}

    public record ServiceResponse(
            String id,
            String name,
            String description,
            BigDecimal basePrice,
            Integer durationMinutes
    ) {}

    // ── Vínculo Barbearia ↔ Serviço ───────────────────────────────────────────

    public record BarbershopServiceLinkRequest(
            @NotBlank(message = "service_id_required")
            String serviceId,

            BigDecimal customPrice  // opcional — usa base_price se null
    ) {}

    public record BarbershopServiceUpdateRequest(
            BigDecimal customPrice
    ) {}

    public record BarbershopServiceResponse(
            String id,
            String serviceId,
            String serviceName,
            String serviceDescription,
            Integer durationMinutes,
            BigDecimal basePrice,
            BigDecimal customPrice,   // preço efetivo desta barbearia
            BigDecimal effectivePrice // customPrice ?? basePrice
    ) {}

    // ── Fotos ─────────────────────────────────────────────────────────────────

    public record PhotoAddRequest(
            @NotBlank(message = "image_data_required")
            String imageData,  // Base64 da imagem

            @NotBlank(message = "media_type_required")
            String mediaType   // ex: "image/jpeg", "image/png", "image/webp"
    ) {}


    public record PhotoResponse(
            String id,
            String dataUrl,    // "data:image/jpeg;base64,/9j/4AAQ..."
            LocalDateTime createdAt
    ) {}
}