package com.barberfind.api.dto;

public record BarbershopUpdateRequest(
        String name,
        String description,
        String phone,
        String email,
        String address,
        String neighborhood,
        String city,
        String state,
        String openingTime,
        String closingTime
) {}