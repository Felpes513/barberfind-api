package com.barberfind.api.dto;

public record RegisterBarberResponse(
        String userId,
        String barberId,
        String name,
        String email
) {}