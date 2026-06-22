package com.clubops.club.dto;

import jakarta.validation.constraints.NotBlank;

public record ClubUpdateRequest(
        @NotBlank(message = "Club name is required")
        String name,

        @NotBlank(message = "Season is required")
        String season
) {
}
