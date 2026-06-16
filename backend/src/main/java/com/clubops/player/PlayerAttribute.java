package com.clubops.player;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "player_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id", nullable = false, unique = true)
    private Player player;

    // Ability and reputation
    @Column(name = "current_ability", nullable = false)
    private Integer currentAbility;

    @Column(name = "potential_ability", nullable = false)
    private Integer potentialAbility;

    @Column(name = "current_reputation", nullable = false)
    private Integer currentReputation;

    @Column(name = "home_reputation", nullable = false)
    private Integer homeReputation;

    @Column(name = "world_reputation", nullable = false)
    private Integer worldReputation;

    // Feet
    @Column(name = "left_foot", nullable = false)
    private Integer leftFoot;

    @Column(name = "right_foot", nullable = false)
    private Integer rightFoot;

    // Personal attributes
    private Integer adaptability;
    private Integer ambition;
    private Integer controversy;
    private Integer loyalty;
    private Integer pressure;
    private Integer professionalism;
    private Integer sportsmanship;
    private Integer temperament;

    // Mental attributes
    private Integer aggression;
    private Integer anticipation;
    private Integer bravery;
    private Integer composure;
    private Integer concentration;
    private Integer consistency;
    private Integer decisions;
    private Integer determination;
    private Integer dirtiness;
    private Integer flair;

    @Column(name = "important_matches")
    private Integer importantMatches;

    private Integer leadership;
    private Integer movement;
    private Integer positioning;

    @Column(name = "team_work")
    private Integer teamWork;

    private Integer vision;

    @Column(name = "work_rate")
    private Integer workRate;

    // Physical attributes
    private Integer acceleration;
    private Integer agility;
    private Integer balance;

    @Column(name = "injury_proneness")
    private Integer injuryProneness;

    @Column(name = "jumping_reach")
    private Integer jumpingReach;

    @Column(name = "natural_fitness")
    private Integer naturalFitness;

    private Integer pace;
    private Integer stamina;
    private Integer strength;

    // Technical attributes
    private Integer corners;
    private Integer crossing;
    private Integer dribbling;
    private Integer finishing;

    @Column(name = "first_touch")
    private Integer firstTouch;

    @Column(name = "free_kicks")
    private Integer freeKicks;

    private Integer heading;

    @Column(name = "long_shots")
    private Integer longShots;

    @Column(name = "long_throws")
    private Integer longThrows;

    private Integer marking;
    private Integer passing;

    @Column(name = "penalty_taking")
    private Integer penaltyTaking;

    private Integer tackling;
    private Integer technique;
    private Integer versatility;

    // Goalkeeping attributes
    @Column(name = "aerial_ability")
    private Integer aerialAbility;

    @Column(name = "command_of_area")
    private Integer commandOfArea;

    private Integer communication;
    private Integer eccentricity;
    private Integer handling;
    private Integer kicking;

    @Column(name = "one_on_ones")
    private Integer oneOnOnes;

    private Integer reflexes;

    @Column(name = "rushing_out")
    private Integer rushingOut;

    @Column(name = "tendency_to_punch")
    private Integer tendencyToPunch;

    private Integer throwing;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        validateAttributes();

        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        validateAttributes();
        this.updatedAt = Instant.now();
    }

    private void validateAttributes() {
        validateRange(currentAbility, 0, 200, "currentAbility");
        validateRange(potentialAbility, 0, 200, "potentialAbility");

        if (potentialAbility < currentAbility) {
            throw new IllegalArgumentException("Potential ability cannot be lower than current ability");
        }

        validateRange(currentReputation, 0, 200, "currentReputation");
        validateRange(homeReputation, 0, 200, "homeReputation");
        validateRange(worldReputation, 0, 200, "worldReputation");

        validateRange(leftFoot, 0, 20, "leftFoot");
        validateRange(rightFoot, 0, 20, "rightFoot");
    }

    private void validateRange(Integer value, int min, int max, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        if (value < min || value > max) {
            throw new IllegalArgumentException(fieldName + " must be between " + min + " and " + max);
        }
    }
}