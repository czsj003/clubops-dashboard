package com.clubops.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerPositionRepository extends JpaRepository<PlayerPosition, Long> {

    List<PlayerPosition> findByPlayer(Player player);
}