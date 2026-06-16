package com.clubops.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerAttributeRepository extends JpaRepository<PlayerAttribute, Long> {

    Optional<PlayerAttribute> findByPlayer(Player player);
}