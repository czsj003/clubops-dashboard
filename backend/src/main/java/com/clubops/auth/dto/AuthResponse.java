package com.clubops.auth.dto;

import com.clubops.user.UserResponse;

public record AuthResponse(
        String token,
        UserResponse user
) {
}