package com.barberfind.api.dto;

import java.time.LocalDateTime;

public record UserPreferencesResponse(
        String id,
        String theme,
        String language,
        LocalDateTime updatedAt
) {}