package com.clubops.player;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "player_languages",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"player_id", "language_code"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerLanguage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "language_code", nullable = false)
    private LanguageCode languageCode;

    @Column(nullable = false)
    private Integer fluency;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        validateFluency();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        validateFluency();
        this.updatedAt = Instant.now();
    }

    private void validateFluency() {
        if (fluency == null || fluency < 1 || fluency > 10) {
            throw new IllegalArgumentException("Language fluency must be between 1 and 10");
        }
    }
}