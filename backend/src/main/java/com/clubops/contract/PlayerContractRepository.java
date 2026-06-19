package com.clubops.contract;

import com.clubops.club.Club;
import com.clubops.player.Player;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerContractRepository extends JpaRepository<PlayerContract, Long> {

    @EntityGraph(attributePaths = {"club", "team", "player"})
    Optional<PlayerContract> findByPlayer(Player player);

    @EntityGraph(attributePaths = {"club", "team", "player"})
    List<PlayerContract> findByClubOrderByTeamDisplayOrderAscPlayerDisplayNameAsc(Club club);
}
