package com.barberfind.api.service;

import com.barberfind.api.domain.BarbershopBarber;
import com.barberfind.api.dto.BarbershopBarberDtos;
import com.barberfind.api.dto.BarbershopBarberDtos.*;
import com.barberfind.api.repository.*;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.shared.util.Cuid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BarbershopBarberService {

    private final BarbershopBarberRepository linkRepo;
    private final BarbershopRepository barbershopRepo;
    private final BarberRepository barberRepo;
    private final BarberAvailabilityRepository availabilityRepo;
    private final BarberPortfolioRepository portfolioRepo;
    private final BarberServiceRepository barberServiceRepo;
    private final ServiceRepository serviceRepo;

    @Transactional
    public LinkStatusResponse requestEntry(String barbershopId) {
        String userId = AuthUtils.getUserId();

        var barber = barberRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "user_is_not_a_barber"));

        barbershopRepo.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        if (linkRepo.existsByBarbershopIdAndBarberId(barbershopId, barber.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "barber_already_linked");
        }

        var link = BarbershopBarber.builder()
                .id(Cuid.generate())
                .barbershopId(barbershopId)
                .barberId(barber.getId())
                .active(false)
                .build();

        linkRepo.save(link);
        return new LinkStatusResponse(link.getId(), barbershopId, barber.getId(), false, "entry_requested_pending_approval");
    }

    @Transactional
    public LinkStatusResponse approve(String barbershopId, String linkId) {
        checkOwner(barbershopId);
        var link = getLinkOrThrow(barbershopId, linkId);
        link.setActive(true);
        linkRepo.save(link);
        return new LinkStatusResponse(link.getId(), barbershopId, link.getBarberId(), true, "barber_approved");
    }

    @Transactional
    public LinkStatusResponse reject(String barbershopId, String linkId) {
        checkOwner(barbershopId);
        var link = getLinkOrThrow(barbershopId, linkId);
        link.setActive(false);
        linkRepo.save(link);
        return new LinkStatusResponse(link.getId(), barbershopId, link.getBarberId(), false, "barber_deactivated");
    }

    @Transactional
    public void remove(String barbershopId, String linkId) {
        String userId = AuthUtils.getUserId();
        String role = AuthUtils.getRole();
        var link = getLinkOrThrow(barbershopId, linkId);

        if ("OWNER".equals(role)) {
            checkOwner(barbershopId);
        } else if ("BARBER".equals(role)) {
            var barber = barberRepo.findByUserId(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden"));
            if (!link.getBarberId().equals(barber.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
            }
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        linkRepo.delete(link);
    }

    @Transactional(readOnly = true)
    public List<BarbershopBarberResponse> list(String barbershopId) {
        barbershopRepo.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        return linkRepo.findAllByBarbershopId(barbershopId).stream()
                .map(this::buildBarberResponse)
                .toList();
    }

    private BarbershopBarberResponse buildBarberResponse(BarbershopBarber link) {
        var barber = barberRepo.findById(link.getBarberId()).orElseThrow();

        var services = barberServiceRepo.findAllByBarberId(barber.getId()).stream()
                .map(bs -> serviceRepo.findById(bs.getServiceId())
                        .map(s -> new BarbershopBarberDtos.ServiceInfo(s.getId(), s.getName(), s.getDurationMinutes(), s.getBasePrice()))
                        .orElse(null))
                .filter(s -> s != null)
                .toList();

        var availability = availabilityRepo.findAllByBarberId(barber.getId()).stream()
                .map(a -> new BarbershopBarberDtos.AvailabilityInfo(
                        a.getWeekday(),
                        a.getStartTime() != null ? a.getStartTime().toString() : null,
                        a.getEndTime() != null ? a.getEndTime().toString() : null))
                .toList();

        var portfolio = portfolioRepo.findAllByBarberId(barber.getId()).stream()
                .map(p -> new BarbershopBarberDtos.PortfolioPhotoInfo(p.getId(), p.getImageUrl(), p.getHairType(), p.getStyleTag(), p.getCreatedAt()))
                .toList();

        return new BarbershopBarberResponse(
                link.getId(), link.getActive(),
                barber.getId(),
                barber.getUser() != null ? barber.getUser().getId() : null,
                barber.getName(), barber.getBio(), barber.getYearsExperience(), barber.getRating(),
                services, availability, portfolio
        );
    }

    private void checkOwner(String barbershopId) {
        String userId = AuthUtils.getUserId();
        barbershopRepo.findByIdAndOwnerUserId(barbershopId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "not_owner_or_barbershop_not_found"));
    }

    private BarbershopBarber getLinkOrThrow(String barbershopId, String linkId) {
        var link = linkRepo.findById(linkId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "link_not_found"));
        if (!link.getBarbershopId().equals(barbershopId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "link_not_found");
        }
        return link;
    }
}