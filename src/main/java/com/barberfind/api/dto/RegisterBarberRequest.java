package com.barberfind.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterBarberRequest(
        @NotBlank(message = "name_required")
        String name,

        @Email(message = "email_invalid")
        @NotBlank(message = "email_required")
        String email,

        @NotBlank(message = "password_required")
        @Size(min = 6, max = 72, message = "password_length_invalid")
        String password,

        String phone,

        // Barber fields
        String bio,

        @Min(value = 0, message = "yearsExperience_invalid")
        Integer yearsExperience
) {}

