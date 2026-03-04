package com.barberfind.api.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Todos os campos são opcionais (null = não alterar).
 */
public record UserPreferencesRequest(

        /**
         * Tema do aplicativo: "LIGHT" ou "DARK"
         */
        @Pattern(regexp = "^(LIGHT|DARK)$", message = "theme_invalid")
        String theme,

        /**
         * Código de idioma: ex. "pt-BR", "en-US", "es-ES"
         */
        @Size(max = 10, message = "language_size_invalid")
        @Pattern(regexp = "^[a-z]{2}(-[A-Z]{2})?$", message = "language_format_invalid")
        String language
) {}