package com.clubops.club.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ClubUpdateRequest(
        @NotBlank(message = "Club name is required")
        String name,

        @NotBlank(message = "Season is required")
        String season,

        @Min(value = 1, message = "Reputation must be at least 1")
        @Max(value = 100, message = "Reputation cannot be greater than 100")
        Integer reputation
) {
}
