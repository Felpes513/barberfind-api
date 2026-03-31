package com.barberfind.api.service;

import com.barberfind.api.domain.*;
import com.barberfind.api.dto.ReviewDtos.*;
import com.barberfind.api.repository.*;
import com.barberfind.api.shared.util.Cuid;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository      reviewRepository;
    private final UserRepository        userRepository;
    private final BarbershopRepository  barbershopRepository;
    private final BarberRepository      barberRepository;
    private final AppointmentRepository appointmentRepository;

    // ── Criar review ──────────────────────────────────────────────

    @Transactional
    public ReviewResponse create(CreateReviewRequest req, String authUserId) {

        // Valida usuário
        var user = userRepository.findById(authUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user_not_found"));

        // Valida barbearia
        var barbershop = barbershopRepository.findById(req.barbershopId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        // Valida se tem agendamento COMPLETED na barbearia
        boolean hasCompletedAppointment = appointmentRepository
                .existsByUserIdAndBarbershopIdAndStatus(authUserId, req.barbershopId(), AppointmentStatus.COMPLETED);

        if (!hasCompletedAppointment) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "no_completed_appointment");
        }

        // Valida duplicata por barbearia
        if (reviewRepository.existsByUser_IdAndBarbershopId(authUserId, req.barbershopId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "review_already_exists_for_barbershop");
        }

        // Resolve barbeiro (opcional)
        BarberEntity barber = null;
        if (req.barberId() != null) {
            barber = barberRepository.findById(req.barberId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_not_found"));

            // Valida duplicata por barbeiro
            if (reviewRepository.existsByUser_IdAndBarber_Id(authUserId, req.barberId())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "review_already_exists_for_barber");
            }
        }

        var review = Review.builder()
                .id(Cuid.generate())
                .user(user)
                .barbershop(barbershop)
                .barber(barber)
                .rating(req.rating())
                .comment(req.comment())
                .build();

        return toResponse(reviewRepository.save(review));
    }

    // ── Listar por barbearia ──────────────────────────────────────

    public List<ReviewResponse> listByBarbershop(String barbershopId) {
        if (!barbershopRepository.existsById(barbershopId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found");
        }

        return reviewRepository.findAllByBarbershopId(barbershopId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Listar por barbeiro ───────────────────────────────────────

    public List<ReviewResponse> listByBarber(String barberId) {
        if (!barberRepository.existsById(barberId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "barber_not_found");
        }

        return reviewRepository.findAllByBarber_Id(barberId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Deletar review ────────────────────────────────────────────

    @Transactional
    public void delete(String barbershopId, String reviewId, String authUserId) {

        var barbershop = barbershopRepository.findById(barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "barbershop_not_found"));

        // Apenas OWNER da barbearia pode deletar
        if (!barbershop.getOwnerUserId().equals(authUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "forbidden");
        }

        var review = reviewRepository.findByIdAndBarbershopId(reviewId, barbershopId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "review_not_found"));

        reviewRepository.delete(review);
    }

    // ── Helper ────────────────────────────────────────────────────

    private ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getName(),
                r.getBarbershop().getId(),
                r.getBarbershop().getName(),
                r.getBarber() != null ? r.getBarber().getId()   : null,
                r.getBarber() != null ? r.getBarber().getName() : null,
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}