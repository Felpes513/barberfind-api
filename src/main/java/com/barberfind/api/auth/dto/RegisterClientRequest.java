package com.barberfind.api.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterClientRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 6, max = 72) String password,
        @Size(max = 30) String phone,
        LocalDate birthDate,
        @Size(max = 255) String hairType,
        @Size(max = 255) String hairTexture,
        Boolean hasBeard
) {}
