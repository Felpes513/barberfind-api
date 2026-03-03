package com.barberfind.api.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @Column(length = 40)
    private String id;

    @Column(name = "user_id", length = 40, nullable = false)
    private String userId;

    @Column(name = "barbershop_id", length = 40, nullable = false)
    private String barbershopId;

    @Column(name = "barber_id", length = 40, nullable = false)
    private String barberId;

    @Column(name = "service_id", length = 40, nullable = false)
    private String serviceId;

    @Column(name = "scheduled_at", nullable = false)
    private LocalDateTime scheduledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AppointmentStatus status;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Column(name = "final_price")
    private BigDecimal finalPrice;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }
}