package com.barberfind.api.dto;

public record BarbershopCreateRequest(
        String name,
        String cnpj,
        String description,
        String phone,
        String email,
        String address,
        String neighborhood,
        String city,
        String state,
        String openingTime,   // "09:00"
        String closingTime,   // "18:00"
        Boolean isHeadquarter,
        String parentBarbershopId
) {}