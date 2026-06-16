package com.clubops.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerSecondaryNationalityRepository extends JpaRepository<PlayerSecondaryNationality, Long> {

    List<PlayerSecondaryNationality> findByPlayer(Player player);
}