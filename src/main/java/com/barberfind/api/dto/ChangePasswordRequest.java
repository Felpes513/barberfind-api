package com.barberfind.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(

        @NotBlank(message = "current_password_required")
        String currentPassword,

        @NotBlank(message = "new_password_required")
        @Size(min = 8, max = 128, message = "password_size_invalid")
        String newPassword
) {}