package com.clubops.auth;

import com.clubops.auth.dto.RegisterRequest;
import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegistrationPolicyServiceTests {

    @Test
    void openModeAllowsRegistrationWithoutInviteCode() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.OPEN,
                ""
        );

        assertThatNoException().isThrownBy(() ->
                service.validateRegistration(request(null))
        );
    }

    @Test
    void inviteOnlyModeRequiresInviteCode() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.INVITE_ONLY,
                "private-code"
        );

        assertThatThrownBy(() -> service.validateRegistration(request(null)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invite code is required.");
    }

    @Test
    void inviteOnlyModeRejectsInvalidInviteCode() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.INVITE_ONLY,
                "private-code"
        );

        assertThatThrownBy(() -> service.validateRegistration(request("wrong")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid invite code.");
    }

    @Test
    void inviteOnlyModeAllowsMatchingInviteCode() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.INVITE_ONLY,
                "private-code"
        );

        assertThatNoException().isThrownBy(() ->
                service.validateRegistration(request(" private-code "))
        );
    }

    @Test
    void inviteOnlyModeFailsFastWhenServerInviteCodeIsMissing() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.INVITE_ONLY,
                ""
        );

        assertThatThrownBy(() -> service.validateRegistration(request("anything")))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(
                        "Invite-only registration is enabled, but no invite code is configured."
                );
    }

    @Test
    void disabledModeRejectsRegistration() {
        RegistrationPolicyService service = new RegistrationPolicyService(
                RegistrationMode.DISABLED,
                ""
        );

        assertThatThrownBy(() -> service.validateRegistration(request("private-code")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Registration is currently disabled.");
    }

    private RegisterRequest request(String inviteCode) {
        return new RegisterRequest(
                "Coach",
                "coach@example.com",
                "password",
                "Northbridge FC",
                Country.ENGLAND,
                FootballLeague.EFL_CHAMPIONSHIP,
                null,
                inviteCode
        );
    }
}
