package com.barberfind.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class DocumentDtos {

    public record DocumentRequest(
            @NotBlank(message = "document_type_required")
            @Size(max = 50)
            String documentType,

            @NotBlank(message = "document_number_required")
            @Size(max = 50)
            String documentNumber
    ) {}

    public record DocumentUpdateRequest(
            @Size(max = 50)
            String documentType,

            @Size(max = 50)
            String documentNumber
    ) {}

    public record DocumentResponse(
            String id,
            String documentType,
            String documentNumber,
            LocalDateTime createdAt
    ) {}
}