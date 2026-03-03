package com.barberfind.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Todos os campos são opcionais (null = não alterar).
 * Campos de barbeiro (bio, yearsExperience) só são aplicados se o usuário tiver role BARBER.
 */
public record UpdateUserRequest(

        @Size(max = 255)
        String name,

        @Pattern(regexp = "^[0-9+()\\-\\s]{10,20}$", message = "invalid_phone")
        String phone,

        LocalDate birthDate,

        @Size(max = 255)
        String hairType,

        @Size(max = 255)
        String hairTexture,

        Boolean hasBeard,

        // Campos exclusivos de barbeiro
        String bio,

        @Min(value = 0, message = "yearsExperience_invalid")
        Integer yearsExperience
) {}