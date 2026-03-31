package com.barberfind.api.controller;

import com.barberfind.api.dto.ReviewDtos.*;
import com.barberfind.api.security.AuthUtils;
import com.barberfind.api.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // POST /api/reviews
    @PostMapping("/api/reviews")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody CreateReviewRequest req
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.create(req, AuthUtils.getUserId()));
    }

    // GET /api/barbershops/{barbershopId}/reviews
    @GetMapping("/api/barbershops/{barbershopId}/reviews")
    public ResponseEntity<List<ReviewResponse>> listByBarbershop(
            @PathVariable String barbershopId
    ) {
        return ResponseEntity.ok(reviewService.listByBarbershop(barbershopId));
    }

    // GET /api/barbers/{barberId}/reviews
    @GetMapping("/api/barbers/{barberId}/reviews")
    public ResponseEntity<List<ReviewResponse>> listByBarber(
            @PathVariable String barberId
    ) {
        return ResponseEntity.ok(reviewService.listByBarber(barberId));
    }

    // DELETE /api/barbershops/{barbershopId}/reviews/{reviewId}
    @DeleteMapping("/api/barbershops/{barbershopId}/reviews/{reviewId}")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> delete(
            @PathVariable String barbershopId,
            @PathVariable String reviewId
    ) {
        reviewService.delete(barbershopId, reviewId, AuthUtils.getUserId());
        return ResponseEntity.noContent().build();
    }
}