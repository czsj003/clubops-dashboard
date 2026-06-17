package com.clubops.contract;

import com.clubops.club.Club;
import com.clubops.currency.CurrencyCode;
import com.clubops.player.Player;
import com.clubops.team.Team;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "player_contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerContract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    @Column(name = "squad_number")
    private Integer squadNumber;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "contract_type", nullable = false)
    private PlayerContractType contractType;

    @Column(name = "wage_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal wageAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_currency", nullable = false)
    private CurrencyCode wageCurrency;

    @Enumerated(EnumType.STRING)
    @Column(name = "wage_display_period", nullable = false)
    private WageDisplayPeriod wageDisplayPeriod;

    @Column(name = "release_clause_amount", precision = 15, scale = 2)
    private BigDecimal releaseClauseAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "release_clause_currency")
    private CurrencyCode releaseClauseCurrency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        validateContract();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        validateContract();
        this.updatedAt = Instant.now();
    }

    private void validateContract() {
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("Contract end date cannot be before start date");
        }

        if (wageAmount == null || wageAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Wage amount cannot be negative");
        }

        if (squadNumber != null && (squadNumber < 1 || squadNumber > 99)) {
            throw new IllegalArgumentException("Squad number must be between 1 and 99");
        }

        if (releaseClauseAmount != null && releaseClauseAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Release clause amount cannot be negative");
        }
    }
}