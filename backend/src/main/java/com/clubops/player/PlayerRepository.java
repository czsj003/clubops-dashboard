package com.clubops.player;

import com.clubops.club.Club;
import com.clubops.team.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    @EntityGraph(attributePaths = {"club", "team"})
    List<Player> findByClub(Club club);

    @EntityGraph(attributePaths = {"club", "team"})
    List<Player> findByTeam(Team team);

    @EntityGraph(attributePaths = {"club", "team"})
    Optional<Player> findWithClubAndTeamById(Long id);

    long countByClub(Club club);
}