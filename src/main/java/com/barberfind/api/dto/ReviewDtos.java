package com.barberfind.api.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

public class ReviewDtos {

    public record CreateReviewRequest(

            @NotBlank(message = "barbershop_id_required")
            String barbershopId,

            String barberId, // opcional — avalia barbearia + barbeiro juntos se informado

            @NotNull(message = "rating_required")
            @Min(value = 1, message = "rating_min_1")
            @Max(value = 5, message = "rating_max_5")
            Integer rating,

            String comment
    ) {}

    public record ReviewResponse(
            String id,
            String userId,
            String userName,
            String barbershopId,
            String barbershopName,
            String barberId,
            String barberName,
            Integer rating,
            String comment,
            LocalDateTime createdAt
    ) {}
}