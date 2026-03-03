package com.barberfind.api.dto;

import com.barberfind.api.domain.AppointmentStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AppointmentDtos {

    // ── Create ────────────────────────────────────────────────────────────────

    public record AppointmentCreateRequest(
            @NotBlank(message = "barbershop_id_required")
            String barbershopId,

            @NotBlank(message = "barber_id_required")
            String barberId,

            @NotBlank(message = "service_id_required")
            String serviceId,

            @NotNull(message = "scheduled_at_required")
            @Future(message = "scheduled_at_must_be_future")
            LocalDateTime scheduledAt,

            String paymentMethod
    ) {}

    // ── Cancel ────────────────────────────────────────────────────────────────

    public record AppointmentCancelRequest(
            String cancellationReason
    ) {}

    // ── Complete ──────────────────────────────────────────────────────────────

    public record AppointmentCompleteRequest(
            BigDecimal finalPrice,
            String paymentMethod
    ) {}

    // ── Response ──────────────────────────────────────────────────────────────

    public record AppointmentResponse(
            String id,
            String userId,
            String barbershopId,
            String barberId,
            String serviceId,
            String serviceName,
            Integer durationMinutes,
            BigDecimal finalPrice,
            String paymentMethod,
            LocalDateTime scheduledAt,
            LocalDateTime completedAt,
            AppointmentStatus status,
            String cancellationReason,
            LocalDateTime createdAt
    ) {}
}