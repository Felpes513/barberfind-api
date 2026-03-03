package com.barberfind.api.dto;

public record BarbershopResponse(
        String id,
        String ownerUserId,
        String name,
        String cnpj,
        String description,
        String phone,
        String email,
        String address,
        String neighborhood,
        String city,
        String state,
        String openingTime,
        String closingTime,
        boolean isActive,
        boolean isHeadquarter,
        String parentBarbershopId
) {}