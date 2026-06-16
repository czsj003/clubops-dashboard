package com.clubops.auth;

import com.clubops.auth.dto.AuthResponse;
import com.clubops.auth.dto.LoginRequest;
import com.clubops.auth.dto.RegisterRequest;
import com.clubops.club.Club;
import com.clubops.club.ClubRepository;
import com.clubops.club.Country;
import com.clubops.security.JwtService;
import com.clubops.team.Team;
import com.clubops.team.TeamRepository;
import com.clubops.team.system.TeamSystemFactory;
import com.clubops.user.User;
import com.clubops.user.UserRepository;
import com.clubops.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClubRepository clubRepository;
    private final TeamRepository teamRepository;
    private final TeamSystemFactory teamSystemFactory;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email is already registered");
        }

        User user = User.builder()
                .name(request.name())
                .email(normalizedEmail)
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        User savedUser = userRepository.save(user);

        Country country = request.country() != null
                ? request.country()
                : Country.ENGLAND;

        String clubName = request.clubName() != null && !request.clubName().isBlank()
                ? request.clubName().trim()
                : "Northbridge FC";

        Club club = Club.builder()
                .user(savedUser)
                .name(clubName)
                .country(country)
                .league(defaultLeagueForCountry(country))
                .season("2026/2027")
                .reputation(70)
                .build();

        Club savedClub = clubRepository.save(club);

        List<Team> defaultTeams = teamSystemFactory.createDefaultTeamsForClub(savedClub);
        teamRepository.saveAll(defaultTeams);

        String token = jwtService.generateToken(savedUser);

        return new AuthResponse(
                token,
                UserResponse.from(savedUser)
        );
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().toLowerCase();

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        normalizedEmail,
                        request.password()
                )
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(user);

        return new AuthResponse(
                token,
                UserResponse.from(user)
        );
    }

    private String defaultLeagueForCountry(Country country) {
        return switch (country) {
            case ENGLAND -> "Championship";
            case SPAIN -> "LaLiga 2";
            case GERMANY -> "2. Bundesliga";
            case ITALY -> "Serie B";
            case FRANCE -> "Ligue 2";
        };
    }
}