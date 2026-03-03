package com.barberfind.api.service;

import com.barberfind.api.domain.Barbershop;
import com.barberfind.api.dto.*;
import com.barberfind.api.dto.BarbershopCreateRequest;
import com.barberfind.api.dto.BarbershopResponse;
import com.barberfind.api.dto.BarbershopStatusRequest;
import com.barberfind.api.dto.BarbershopUpdateRequest;
import com.barberfind.api.repository.BarbershopRepository;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.shared.util.Cuid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
public class BarbershopService {

    private final BarbershopRepository repo;

    public BarbershopService(BarbershopRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public BarbershopResponse create(BarbershopCreateRequest req) {
        String ownerUserId = AuthUtils.getUserId();

        boolean isHeadquarter = req.isHeadquarter() == null ? true : req.isHeadquarter();
        String parentId = req.parentBarbershopId();

        // regra matriz/filial
        if (isHeadquarter && parentId != null && !parentId.isBlank()) {
            throw new IllegalArgumentException("Matriz não pode ter parentBarbershopId.");
        }
        if (!isHeadquarter && (parentId == null || parentId.isBlank())) {
            throw new IllegalArgumentException("Filial precisa de parentBarbershopId.");
        }
        if (!isHeadquarter) {
            repo.findById(parentId).orElseThrow(() ->
                    new IllegalArgumentException("Barbearia matriz (parent) não encontrada.")
            );
        }

        Barbershop b = new Barbershop();
        b.setId(Cuid.generate());
        b.setOwnerUserId(ownerUserId);

        b.setName(req.name());
        b.setCnpj(req.cnpj());
        b.setDescription(req.description());
        b.setPhone(req.phone());
        b.setEmail(req.email());
        b.setAddress(req.address());
        b.setNeighborhood(req.neighborhood());
        b.setCity(req.city());
        b.setState(req.state());
        b.setOpeningTime(parseTime(req.openingTime()));
        b.setClosingTime(parseTime(req.closingTime()));

        b.setHeadquarter(isHeadquarter);
        b.setParentBarbershopId(isHeadquarter ? null : parentId);
        b.setActive(true);

        repo.save(b);
        return toResponse(b);
    }

    @Transactional
    public BarbershopResponse update(String id, BarbershopUpdateRequest req) {
        String ownerUserId = AuthUtils.getUserId();

        Barbershop b = repo.findByIdAndOwnerUserId(id, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada ou não pertence ao dono."));

        if (req.name() != null) b.setName(req.name());
        if (req.description() != null) b.setDescription(req.description());
        if (req.phone() != null) b.setPhone(req.phone());
        if (req.email() != null) b.setEmail(req.email());
        if (req.address() != null) b.setAddress(req.address());
        if (req.neighborhood() != null) b.setNeighborhood(req.neighborhood());
        if (req.city() != null) b.setCity(req.city());
        if (req.state() != null) b.setState(req.state());
        if (req.openingTime() != null) b.setOpeningTime(parseTime(req.openingTime()));
        if (req.closingTime() != null) b.setClosingTime(parseTime(req.closingTime()));

        repo.save(b);
        return toResponse(b);
    }

    @Transactional
    public BarbershopResponse setStatus(String id, BarbershopStatusRequest req) {
        String ownerUserId = AuthUtils.getUserId();

        Barbershop b = repo.findByIdAndOwnerUserId(id, ownerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada ou não pertence ao dono."));

        b.setActive(Boolean.TRUE.equals(req.isActive()));
        repo.save(b);
        return toResponse(b);
    }

    @Transactional(readOnly = true)
    public List<BarbershopResponse> listPublic() {
        return repo.findByIsActiveTrue().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public BarbershopResponse getPublicById(String id) {
        Barbershop b = repo.findById(id)
                .filter(Barbershop::isActive)
                .orElseThrow(() -> new IllegalArgumentException("Barbearia não encontrada."));
        return toResponse(b);
    }

    @Transactional(readOnly = true)
    public List<BarbershopResponse> listMine() {
        String ownerUserId = AuthUtils.getUserId();
        return repo.findByOwnerUserId(ownerUserId).stream().map(this::toResponse).toList();
    }

    private LocalTime parseTime(String s) {
        if (s == null || s.isBlank()) return null;
        return LocalTime.parse(s); // "HH:mm"
    }

    private BarbershopResponse toResponse(Barbershop b) {
        return new BarbershopResponse(
                b.getId(),
                b.getOwnerUserId(),
                b.getName(),
                b.getCnpj(),
                b.getDescription(),
                b.getPhone(),
                b.getEmail(),
                b.getAddress(),
                b.getNeighborhood(),
                b.getCity(),
                b.getState(),
                b.getOpeningTime() == null ? null : b.getOpeningTime().toString(),
                b.getClosingTime() == null ? null : b.getClosingTime().toString(),
                b.isActive(),
                b.isHeadquarter(),
                b.getParentBarbershopId()
        );
    }
}