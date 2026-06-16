package com.clubops.player;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerLanguageRepository extends JpaRepository<PlayerLanguage, Long> {

    List<PlayerLanguage> findByPlayer(Player player);
}