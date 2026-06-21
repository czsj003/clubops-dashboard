package com.clubops.player;

import com.clubops.club.Club;
import com.clubops.team.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;
import java.math.BigDecimal;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Ownership
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    // Name
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "common_name")
    private String commonName;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Appearance
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Race race;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_color", nullable = false)
    private HairColor hairColor;

    @Enumerated(EnumType.STRING)
    @Column(name = "hair_length", nullable = false)
    private HairLength hairLength;

    @Enumerated(EnumType.STRING)
    @Column(name = "skin_tone", nullable = false)
    private SkinTone skinTone;

    // Physical profile
    @Column(name = "height_cm", nullable = false)
    private Integer heightCm;

    @Column(name = "weight_kg", nullable = false)
    private Integer weightKg;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "birth_city", nullable = false)
    private String birthCity;

    @Enumerated(EnumType.STRING)
    @Column(name = "birth_country", nullable = false)
    private CountryCode birthCountry;

    @Enumerated(EnumType.STRING)
    @Column(name = "nationality", nullable = false)
    private CountryCode nationality;

    @Column(name = "estimated_value_in_gbp", precision = 15, scale = 2)
    private BigDecimal estimatedValueInGbp;

    // Display helper
    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        prepareNames();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        prepareNames();
        this.updatedAt = Instant.now();
    }

    private void prepareNames() {
        if (this.fullName == null || this.fullName.isBlank()) {
            this.fullName = this.firstName + " " + this.lastName;
        }

        if (this.commonName != null && !this.commonName.isBlank()) {
            this.displayName = this.commonName;
        } else {
            this.displayName = this.firstName + " " + this.lastName;
        }
    }

    public int getAge() {
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }
}
