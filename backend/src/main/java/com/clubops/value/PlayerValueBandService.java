package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.value.dto.PlayerValueBandRequest;
import com.clubops.value.dto.PlayerValueBandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlayerValueBandService {

    private final PlayerValueBandRepository repository;

    public List<PlayerValueBandResponse> getBands(Country country) {
        return repository.findByCountryOrderByReputationMinAsc(country)
                .stream()
                .map(PlayerValueBandResponse::from)
                .toList();
    }

    @Transactional
    public PlayerValueBandResponse createBand(PlayerValueBandRequest request) {
        validate(request, null);

        PlayerValueBand band = PlayerValueBand.builder()
                .country(request.country())
                .reputationMin(request.reputationMin())
                .reputationMax(request.reputationMax())
                .baseValue(request.baseValue())
                .currency(request.currency())
                .build();

        return PlayerValueBandResponse.from(repository.save(band));
    }

    @Transactional
    public PlayerValueBandResponse updateBand(
            Long id,
            PlayerValueBandRequest request
    ) {
        PlayerValueBand band = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Value band not found"));

        validate(request, id);

        band.setCountry(request.country());
        band.setReputationMin(request.reputationMin());
        band.setReputationMax(request.reputationMax());
        band.setBaseValue(request.baseValue());
        band.setCurrency(request.currency());

        return PlayerValueBandResponse.from(repository.save(band));
    }

    @Transactional
    public void deleteBand(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Value band not found");
        }
        repository.deleteById(id);
    }

    private void validate(PlayerValueBandRequest request, Long excludeId) {
        if (request.reputationMin() > request.reputationMax()) {
            throw new IllegalArgumentException(
                    "Reputation minimum cannot be greater than maximum"
            );
        }

        if (repository.existsOverlappingBand(
                request.country(),
                request.reputationMin(),
                request.reputationMax(),
                excludeId
        )) {
            throw new IllegalArgumentException(
                    "Reputation range overlaps an existing value band"
            );
        }
    }

}
