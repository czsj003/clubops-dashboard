package com.clubops.player;

import com.clubops.club.Club;
import com.clubops.team.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByClub(Club club);

    List<Player> findByTeam(Team team);

    long countByClub(Club club);
}