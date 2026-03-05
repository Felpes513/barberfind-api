package com.barberfind.api.service;

import com.barberfind.api.domain.Barbershop;
import com.barberfind.api.domain.Favorite;
import com.barberfind.api.dto.FavoriteDtos.*;
import com.barberfind.api.repository.BarberRepository;
import com.barberfind.api.repository.BarbershopRepository;
import com.barberfind.api.repository.FavoriteRepository;
import com.barberfind.api.repository.UserRepository;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BarberRepository barberRepository;
    private final BarbershopRepository barbershopRepository;

    // ── List ──────────────────────────────────────────────────────────────────

    public List<FavoriteResponse> list(String targetUserId, String authenticatedUserId) {
        checkOwnership(targetUserId, authenticatedUserId);

        return favoriteRepository.findAllByUserId(targetUserId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Add ───────────────────────────────────────────────────────────────────

    @Transactional
    public FavoriteResponse add(String targetUserId, String authenticatedUserId, FavoriteRequest req) {
        checkOwnership(targetUserId, authenticatedUserId);
        validateRequest(req);

        var user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        var builder = Favorite.builder()
                .id(Cuid.generate())
                .user(user);

        // Barber favorite
        if (req.barberId() != null) {
            if (favoriteRepository.existsByUserIdAndBarberId(targetUserId, req.barberId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "barber_already_favorited");
            }
            var barber = barberRepository.findById(req.barberId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_not_found"));
            builder.barber(barber);
        }

        // Barbershop favorite
        if (req.barbershopId() != null) {
            if (favoriteRepository.existsByUserIdAndBarbershopId(targetUserId, req.barbershopId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "barbershop_already_favorited");
            }
            var barbershop = barbershopRepository.findById(req.barbershopId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));
            builder.barbershop(barbershop);
        }

        var favorite = favoriteRepository.save(builder.build());
        return toResponse(favorite);
    }

    // ── Remove ────────────────────────────────────────────────────────────────

    @Transactional
    public void remove(String targetUserId, String authenticatedUserId, String favoriteId) {
        checkOwnership(targetUserId, authenticatedUserId);

        var favorite = favoriteRepository.findById(favoriteId)
                .filter(f -> f.getUser().getId().equals(targetUserId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "favorite_not_found"));

        favoriteRepository.delete(favorite);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void validateRequest(FavoriteRequest req) {
        boolean hasBarber     = req.barberId() != null;
        boolean hasBarbershop = req.barbershopId() != null;

        if (!hasBarber && !hasBarbershop) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(422),
                    "barber_id_or_barbershop_id_required");
        }
        if (hasBarber && hasBarbershop) {
            throw new ResponseStatusException(HttpStatusCode.valueOf(422),
                    "only_one_of_barber_id_or_barbershop_id_allowed");
        }
    }

    private FavoriteResponse toResponse(Favorite f) {
        return new FavoriteResponse(
                f.getId(),
                f.getBarber()     != null ? f.getBarber().getId()        : null,
                f.getBarber()     != null ? f.getBarber().getName()      : null,
                f.getBarbershop() != null ? f.getBarbershop().getId()    : null,
                f.getBarbershop() != null ? f.getBarbershop().getName()  : null,
                f.getCreatedAt()
        );
    }

    private void checkOwnership(String targetUserId, String authenticatedUserId) {
        if (!targetUserId.equals(authenticatedUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }
    }
}