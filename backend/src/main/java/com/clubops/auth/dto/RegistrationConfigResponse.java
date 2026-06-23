package com.clubops.auth.dto;

import com.clubops.auth.RegistrationMode;

public record RegistrationConfigResponse(
        RegistrationMode mode
) {
}
