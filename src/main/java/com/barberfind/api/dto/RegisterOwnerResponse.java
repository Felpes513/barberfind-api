package com.barberfind.api.dto;

public record RegisterOwnerResponse(
        String userId,
        String name,
        String email,
        String role
) {}