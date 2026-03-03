package com.barberfind.api.controller;

import com.barberfind.api.dto.AppointmentDtos.*;
import com.barberfind.api.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    // CLIENT cria agendamento
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/api/appointments")
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody AppointmentCreateRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(appointmentService.create(req));
    }

    // CLIENT lista os próprios agendamentos
    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/api/appointments/me")
    public ResponseEntity<List<AppointmentResponse>> listMine() {
        return ResponseEntity.ok(appointmentService.listMine());
    }

    // OWNER ou BARBER lista agendamentos da barbearia
    @PreAuthorize("hasRole('OWNER') or hasRole('BARBER')")
    @GetMapping("/api/barbershops/{barbershopId}/appointments")
    public ResponseEntity<List<AppointmentResponse>> listByBarbershop(@PathVariable String barbershopId) {
        return ResponseEntity.ok(appointmentService.listByBarbershop(barbershopId));
    }

    // BARBER ou OWNER confirma
    @PreAuthorize("hasRole('BARBER') or hasRole('OWNER')")
    @PatchMapping("/api/appointments/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.confirm(id));
    }

    // BARBER ou OWNER conclui
    @PreAuthorize("hasRole('BARBER') or hasRole('OWNER')")
    @PatchMapping("/api/appointments/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(
            @PathVariable String id,
            @RequestBody(required = false) AppointmentCompleteRequest req) {
        return ResponseEntity.ok(appointmentService.complete(id, req));
    }

    // BARBER ou OWNER marca no-show
    @PreAuthorize("hasRole('BARBER') or hasRole('OWNER')")
    @PatchMapping("/api/appointments/{id}/no-show")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable String id) {
        return ResponseEntity.ok(appointmentService.noShow(id));
    }

    // CLIENT, BARBER ou OWNER cancela
    @PatchMapping("/api/appointments/{id}/cancel")
    public ResponseEntity<AppointmentResponse> cancel(
            @PathVariable String id,
            @RequestBody(required = false) AppointmentCancelRequest req) {
        return ResponseEntity.ok(appointmentService.cancel(id, req));
    }
}