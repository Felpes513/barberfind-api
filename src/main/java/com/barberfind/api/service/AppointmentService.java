package com.barberfind.api.service;

import com.barberfind.api.domain.Appointment;
import com.barberfind.api.domain.AppointmentStatus;
import com.barberfind.api.dto.AppointmentDtos.*;
import com.barberfind.api.repository.*;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.shared.util.Cuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepo;
    private final BarbershopRepository barbershopRepo;
    private final BarberRepository barberRepo;
    private final ServiceRepository serviceRepo;
    private final BarbershopBarberRepository barbershopBarberRepo;

    @Transactional
    public AppointmentResponse create(AppointmentCreateRequest req) {
        String userId = AuthUtils.getUserId();

        var barbershop = barbershopRepo.findById(req.barbershopId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));
        if (!barbershop.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "barbershop_inactive");
        }

        barberRepo.findById(req.barberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_not_found"));

        var link = barbershopBarberRepo.findByBarbershopIdAndBarberId(req.barbershopId(), req.barberId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "barber_not_in_barbershop"));
        if (!Boolean.TRUE.equals(link.getActive())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "barber_not_active_in_barbershop");
        }

        var service = serviceRepo.findById(req.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "service_not_found"));

        boolean conflict = appointmentRepo.existsByBarberIdAndScheduledAtAndStatusNot(
                req.barberId(), req.scheduledAt(), AppointmentStatus.CANCELLED);
        if (conflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "barber_time_slot_unavailable");
        }

        var appointment = Appointment.builder()
                .id(Cuid.generate())
                .userId(userId)
                .barbershopId(req.barbershopId())
                .barberId(req.barberId())
                .serviceId(req.serviceId())
                .scheduledAt(req.scheduledAt())
                .status(AppointmentStatus.PENDING)
                .paymentMethod(req.paymentMethod())
                .finalPrice(service.getBasePrice())
                .build();

        appointmentRepo.save(appointment);
        return toResponse(appointment, service.getName(), service.getDurationMinutes());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listMine() {
        String userId = AuthUtils.getUserId();
        return appointmentRepo.findAllByUserIdOrderByScheduledAtDesc(userId).stream()
                .map(a -> toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> listByBarbershop(String barbershopId) {
        String userId = AuthUtils.getUserId();
        String role = AuthUtils.getRole();

        if ("OWNER".equals(role)) {
            barbershopRepo.findByIdAndOwnerUserId(barbershopId, userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden"));
        } else if ("BARBER".equals(role)) {
            var barber = barberRepo.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden"));
            barbershopBarberRepo.findByBarbershopIdAndBarberId(barbershopId, barber.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "barber_not_in_barbershop"));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        return appointmentRepo.findAllByBarbershopIdOrderByScheduledAtDesc(barbershopId).stream()
                .map(a -> toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId())))
                .toList();
    }

    @Transactional
    public AppointmentResponse confirm(String appointmentId) {
        var a = getAndCheckStaff(appointmentId);
        checkStatus(a, AppointmentStatus.PENDING, "only_pending_can_be_confirmed");
        a.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepo.save(a);
        return toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId()));
    }

    @Transactional
    public AppointmentResponse complete(String appointmentId, AppointmentCompleteRequest req) {
        var a = getAndCheckStaff(appointmentId);
        checkStatus(a, AppointmentStatus.CONFIRMED, "only_confirmed_can_be_completed");
        a.setStatus(AppointmentStatus.COMPLETED);
        a.setCompletedAt(LocalDateTime.now());
        if (req != null && req.finalPrice() != null) a.setFinalPrice(req.finalPrice());
        if (req != null && req.paymentMethod() != null) a.setPaymentMethod(req.paymentMethod());
        appointmentRepo.save(a);
        return toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId()));
    }

    @Transactional
    public AppointmentResponse noShow(String appointmentId) {
        var a = getAndCheckStaff(appointmentId);
        checkStatus(a, AppointmentStatus.CONFIRMED, "only_confirmed_can_be_no_show");
        a.setStatus(AppointmentStatus.NO_SHOW);
        appointmentRepo.save(a);
        return toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId()));
    }

    @Transactional
    public AppointmentResponse cancel(String appointmentId, AppointmentCancelRequest req) {
        String userId = AuthUtils.getUserId();
        String role = AuthUtils.getRole();

        Appointment a;
        if ("CLIENT".equals(role)) {
            a = appointmentRepo.findByIdAndUserId(appointmentId, userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment_not_found"));
        } else {
            a = getAndCheckStaff(appointmentId);
        }

        if (a.getStatus() == AppointmentStatus.COMPLETED || a.getStatus() == AppointmentStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cannot_cancel_this_status");
        }

        a.setStatus(AppointmentStatus.CANCELLED);
        a.setCancellationReason(req != null ? req.cancellationReason() : null);
        appointmentRepo.save(a);
        return toResponse(a, getServiceName(a.getServiceId()), getServiceDuration(a.getServiceId()));
    }

    private Appointment getAndCheckStaff(String appointmentId) {
        String userId = AuthUtils.getUserId();
        String role = AuthUtils.getRole();

        var a = appointmentRepo.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "appointment_not_found"));

        if ("OWNER".equals(role)) {
            barbershopRepo.findByIdAndOwnerUserId(a.getBarbershopId(), userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden"));
        } else if ("BARBER".equals(role)) {
            var barber = barberRepo.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden"));
            if (!a.getBarberId().equals(barber.getId()))
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
        return a;
    }

    private void checkStatus(Appointment a, AppointmentStatus expected, String errorKey) {
        if (a.getStatus() != expected)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, errorKey);
    }

    private String getServiceName(String serviceId) {
        return serviceRepo.findById(serviceId).map(s -> s.getName()).orElse(null);
    }

    private Integer getServiceDuration(String serviceId) {
        return serviceRepo.findById(serviceId).map(s -> s.getDurationMinutes()).orElse(null);
    }

    private AppointmentResponse toResponse(Appointment a, String serviceName, Integer duration) {
        return new AppointmentResponse(
                a.getId(), a.getUserId(), a.getBarbershopId(), a.getBarberId(), a.getServiceId(),
                serviceName, duration, a.getFinalPrice(), a.getPaymentMethod(),
                a.getScheduledAt(), a.getCompletedAt(), a.getStatus(),
                a.getCancellationReason(), a.getCreatedAt()
        );
    }
}