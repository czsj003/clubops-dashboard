package com.clubops.team;

import com.clubops.club.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findByClubOrderByDisplayOrderAsc(Club club);

    boolean existsByClub(Club club);
}