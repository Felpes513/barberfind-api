package com.barberfind.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class BarbershopBarberDtos {

    // ── Response completo do barbeiro ─────────────────────────────────────────

    public record AvailabilityInfo(
            Integer weekday,    // 0=domingo ... 6=sábado
            String startTime,
            String endTime
    ) {}

    public record PortfolioPhotoInfo(
            String id,
            String imageUrl,
            String hairType,
            String styleTag,
            LocalDateTime createdAt
    ) {}

    public record ServiceInfo(
            String serviceId,
            String serviceName,
            Integer durationMinutes,
            BigDecimal basePrice
    ) {}

    public record BarbershopBarberResponse(
            String linkId,           // id em barbershop_barbers
            Boolean active,          // aprovado ou pendente

            // dados do barbeiro
            String barberId,
            String userId,
            String name,
            String bio,
            Integer yearsExperience,
            BigDecimal rating,

            List<ServiceInfo> services,
            List<AvailabilityInfo> availability,
            List<PortfolioPhotoInfo> portfolio
    ) {}

    // ── Resposta simples para operações de vínculo ────────────────────────────

    public record LinkStatusResponse(
            String linkId,
            String barbershopId,
            String barberId,
            Boolean active,
            String message
    ) {}
}