package com.barberfind.api.repository;

import com.barberfind.api.domain.Appointment;
import com.barberfind.api.domain.AppointmentStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, String> {

    List<Appointment> findAllByUserIdOrderByScheduledAtDesc(String userId);

    List<Appointment> findAllByBarbershopIdOrderByScheduledAtDesc(String barbershopId);

    List<Appointment> findAllByBarberIdOrderByScheduledAtDesc(String barberId);

    // verifica conflito de horário para o barbeiro
    boolean existsByBarberIdAndScheduledAtAndStatusNot(
            String barberId, LocalDateTime scheduledAt, AppointmentStatus status);

    Optional<Appointment> findByIdAndUserId(String id, String userId);

    boolean existsByUserIdAndBarbershopIdAndStatus(String userId, String barbershopId, AppointmentStatus status);
}