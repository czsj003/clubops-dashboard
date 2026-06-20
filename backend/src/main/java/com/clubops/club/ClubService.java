package com.clubops.club;

import com.clubops.club.dto.ClubResponse;
import com.clubops.club.dto.ClubUpdateRequest;
import com.clubops.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository clubRepository;

    public ClubResponse getCurrentUserClub(User user) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        return ClubResponse.from(club);
    }

    @Transactional
    public ClubResponse updateCurrentUserClub(User user, ClubUpdateRequest request) {
        Club club = clubRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Club not found for current user"));

        club.setName(request.name());
        club.setSeason(request.season());
        club.setReputation(request.reputation());

        return ClubResponse.from(club);
    }
}
