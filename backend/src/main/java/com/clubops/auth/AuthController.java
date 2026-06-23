package com.clubops.auth;

import com.clubops.auth.dto.AuthResponse;
import com.clubops.auth.dto.LoginRequest;
import com.clubops.auth.dto.RegistrationConfigResponse;
import com.clubops.auth.dto.RegisterRequest;
import com.clubops.user.User;
import com.clubops.user.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RegistrationPolicyService registrationPolicyService;

    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/registration-config")
    public RegistrationConfigResponse getRegistrationConfig() {
        return new RegistrationConfigResponse(
                registrationPolicyService.getRegistrationMode()
        );
    }

    @GetMapping("/me")
    public UserResponse me(@AuthenticationPrincipal User user) {
        return UserResponse.from(user);
    }
}
