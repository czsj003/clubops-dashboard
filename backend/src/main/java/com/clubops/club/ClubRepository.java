package com.clubops.club;

import com.clubops.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClubRepository extends JpaRepository<Club, Long> {

    Optional<Club> findByUser(User user);

    Optional<Club> findByUserId(Long userId);

    boolean existsByUser(User user);
}