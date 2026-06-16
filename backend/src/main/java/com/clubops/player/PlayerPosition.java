package com.clubops.player;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(
        name = "player_positions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"player_id", "position_type"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false)
    private Player player;

    @Enumerated(EnumType.STRING)
    @Column(name = "position_type", nullable = false)
    private PlayerPositionType positionType;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        validateRating();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        validateRating();
        this.updatedAt = Instant.now();
    }

    private void validateRating() {
        if (rating == null || rating < 1 || rating > 20) {
            throw new IllegalArgumentException("Position rating must be between 1 and 20");
        }
    }
}