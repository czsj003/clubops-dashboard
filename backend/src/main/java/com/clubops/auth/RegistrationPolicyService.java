package com.clubops.auth;

import com.clubops.auth.dto.RegisterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RegistrationPolicyService {

    private final RegistrationMode registrationMode;
    private final String inviteCode;

    public RegistrationPolicyService(
            @Value("${app.registration.mode:OPEN}") RegistrationMode registrationMode,
            @Value("${app.registration.invite-code:}") String inviteCode
    ) {
        this.registrationMode = registrationMode;
        this.inviteCode = inviteCode;
    }

    public void validateRegistration(RegisterRequest request) {
        if (registrationMode == RegistrationMode.DISABLED) {
            throw new IllegalArgumentException("Registration is currently disabled.");
        }

        if (registrationMode == RegistrationMode.INVITE_ONLY) {
            validateInviteCode(request.inviteCode());
        }
    }

    public RegistrationMode getRegistrationMode() {
        return registrationMode;
    }

    private void validateInviteCode(String submittedInviteCode) {
        if (inviteCode == null || inviteCode.isBlank()) {
            throw new IllegalStateException(
                    "Invite-only registration is enabled, but no invite code is configured."
            );
        }

        if (submittedInviteCode == null || submittedInviteCode.isBlank()) {
            throw new IllegalArgumentException("Invite code is required.");
        }

        if (!inviteCode.equals(submittedInviteCode.trim())) {
            throw new IllegalArgumentException("Invalid invite code.");
        }
    }
}
