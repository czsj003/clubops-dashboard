package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PlayerValueBandRepository extends JpaRepository<PlayerValueBand, Long> {

    @Query("""
            select band from PlayerValueBand band
            where band.country = :country
              and band.league = :league
              and band.reputationMin <= :reputation
              and band.reputationMax >= :reputation
            """)
    Optional<PlayerValueBand> findMatchingBand(
            @Param("country") Country country,
            @Param("league") FootballLeague league,
            @Param("reputation") Integer reputation
    );

    List<PlayerValueBand> findByCountryAndLeagueOrderByReputationMinAsc(
            Country country,
            FootballLeague league
    );

    @Query("""
            select count(band) > 0 from PlayerValueBand band
            where band.country = :country
              and band.league = :league
              and (:excludeId is null or band.id <> :excludeId)
              and band.reputationMin <= :reputationMax
              and band.reputationMax >= :reputationMin
            """)
    boolean existsOverlappingBand(
            @Param("country") Country country,
            @Param("league") FootballLeague league,
            @Param("reputationMin") Integer reputationMin,
            @Param("reputationMax") Integer reputationMax,
            @Param("excludeId") Long excludeId
    );
}
