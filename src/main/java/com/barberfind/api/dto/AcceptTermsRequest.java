package com.barberfind.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AcceptTermsRequest(

        /**
         * Versão dos termos aceita, ex: "1.0", "2.1"
         */
        @NotBlank(message = "terms_version_required")
        String termsVersion,

        /**
         * O usuário deve confirmar explicitamente o aceite.
         * Se false, a validação rejeita.
         */
        @NotNull(message = "accepted_required")
        Boolean accepted
) {}