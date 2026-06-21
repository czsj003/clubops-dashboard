package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.currency.CurrencyCode;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "player_value_bands",
        uniqueConstraints = @UniqueConstraint(columnNames = {
                "country",
                "league",
                "reputation_min",
                "reputation_max"
        })
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerValueBand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Country country;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FootballLeague league;

    @Column(name = "reputation_min", nullable = false)
    private Integer reputationMin;

    @Column(name = "reputation_max", nullable = false)
    private Integer reputationMax;

    @Column(name = "base_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal baseValue;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CurrencyCode currency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        validate();
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        validate();
        updatedAt = Instant.now();
    }

    private void validate() {
        if (country == null || league == null || currency == null) {
            throw new IllegalArgumentException("Country, league, and currency are required");
        }
        if (reputationMin == null || reputationMax == null
                || reputationMin < 1 || reputationMax > 200
                || reputationMin > reputationMax) {
            throw new IllegalArgumentException("Invalid reputation range");
        }
        if (baseValue == null || baseValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Base value cannot be negative");
        }
    }
}
