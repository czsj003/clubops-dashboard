package com.clubops.auth.dto;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank(message = "Name is required")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters")
        String password,

        String clubName,

        Country country,
        FootballLeague league,
        String leagueGroup
) {
}
