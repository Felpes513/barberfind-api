package com.barberfind.api.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class FavoriteDtos {

    /**
     * Deve enviar APENAS um dos dois campos (barber_id OU barbershop_id).
     * A validação de exclusividade mútua é feita no service.
     */
    public record FavoriteRequest(
            String barberId,
            String barbershopId
    ) {}

    public record FavoriteResponse(
            String id,
            String barberId,
            String barberName,
            String barbershopId,
            String barbershopName,
            LocalDateTime createdAt
    ) {}
}