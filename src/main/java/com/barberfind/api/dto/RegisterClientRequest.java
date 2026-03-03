package com.barberfind.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterClientRequest(
        @NotBlank @Size(max = 255)
        String name,

        @NotBlank @Email @Size(max = 255)
        String email,
        @NotBlank @Size(min = 6, max = 72)
        String password,

        @Pattern(regexp = "^[0-9+()\\-\\s]{10,20}$", message = "invalid_phone")
        String phone,

        LocalDate birthDate,

        @Size(max = 255)
        String hairType,

        @Size(max = 255)
        String hairTexture,

        Boolean hasBeard
) {}
