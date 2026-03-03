package com.barberfind.api.service;

import com.barberfind.api.domain.BarbershopService;
import com.barberfind.api.domain.Service;
import com.barberfind.api.dto.ServiceDtos.*;
import com.barberfind.api.repository.BarbershopRepository;
import com.barberfind.api.repository.BarbershopServiceRepository;
import com.barberfind.api.repository.ServiceRepository;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.shared.util.Cuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GlobalServiceManager {

    private final ServiceRepository serviceRepo;
    private final BarbershopServiceRepository barbershopServiceRepo;
    private final BarbershopRepository barbershopRepo;

    // ── Serviços globais ──────────────────────────────────────────────────────

    @Transactional
    public ServiceResponse createGlobal(ServiceCreateRequest req) {
        var s = Service.builder()
                .id(Cuid.generate())
                .name(req.name())
                .description(req.description())
                .basePrice(req.basePrice())
                .durationMinutes(req.durationMinutes())
                .build();

        serviceRepo.save(s);
        return toServiceResponse(s);
    }

    @Transactional(readOnly = true)
    public List<ServiceResponse> listGlobal() {
        return serviceRepo.findAll().stream().map(this::toServiceResponse).toList();
    }

    // ── Vínculos barbearia ↔ serviço ──────────────────────────────────────────

    @Transactional
    public BarbershopServiceResponse link(String barbershopId, BarbershopServiceLinkRequest req) {
        checkOwner(barbershopId);

        // serviço existe?
        var service = serviceRepo.findById(req.serviceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "service_not_found"));

        // já vinculado?
        if (barbershopServiceRepo.existsByBarbershopIdAndServiceId(barbershopId, req.serviceId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "service_already_linked");
        }

        var link = BarbershopService.builder()
                .id(Cuid.generate())
                .barbershopId(barbershopId)
                .serviceId(req.serviceId())
                .customPrice(req.customPrice())
                .build();

        barbershopServiceRepo.save(link);
        return toBarbershopServiceResponse(link, service);
    }

    @Transactional(readOnly = true)
    public List<BarbershopServiceResponse> listByBarbershop(String barbershopId) {
        return barbershopServiceRepo.findAllByBarbershopId(barbershopId).stream()
                .map(link -> {
                    var service = serviceRepo.findById(link.getServiceId())
                            .orElseThrow();
                    return toBarbershopServiceResponse(link, service);
                })
                .toList();
    }

    @Transactional
    public BarbershopServiceResponse updateLink(String barbershopId, String linkId,
                                                BarbershopServiceUpdateRequest req) {
        checkOwner(barbershopId);

        var link = barbershopServiceRepo.findByIdAndBarbershopId(linkId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "link_not_found"));

        link.setCustomPrice(req.customPrice());
        barbershopServiceRepo.save(link);

        var service = serviceRepo.findById(link.getServiceId()).orElseThrow();
        return toBarbershopServiceResponse(link, service);
    }

    @Transactional
    public void unlink(String barbershopId, String linkId) {
        checkOwner(barbershopId);

        var link = barbershopServiceRepo.findByIdAndBarbershopId(linkId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "link_not_found"));

        barbershopServiceRepo.delete(link);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void checkOwner(String barbershopId) {
        String userId = AuthUtils.getUserId();
        barbershopRepo.findByIdAndOwnerUserId(barbershopId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN,
                        "not_owner_or_barbershop_not_found"));
    }

    private ServiceResponse toServiceResponse(Service s) {
        return new ServiceResponse(s.getId(), s.getName(), s.getDescription(),
                s.getBasePrice(), s.getDurationMinutes());
    }

    private BarbershopServiceResponse toBarbershopServiceResponse(BarbershopService link, Service s) {
        BigDecimal effective = link.getCustomPrice() != null ? link.getCustomPrice() : s.getBasePrice();
        return new BarbershopServiceResponse(
                link.getId(),
                s.getId(),
                s.getName(),
                s.getDescription(),
                s.getDurationMinutes(),
                s.getBasePrice(),
                link.getCustomPrice(),
                effective
        );
    }
}