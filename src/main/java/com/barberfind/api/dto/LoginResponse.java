package com.barberfind.api.dto;

public record LoginResponse(
        String token,
        String userId,
        String role,
        String name,
        String email
) {}