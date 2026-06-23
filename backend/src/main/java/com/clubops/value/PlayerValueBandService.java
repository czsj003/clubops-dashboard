package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.value.dto.PlayerValueBandBulkRequest;
import com.clubops.value.dto.PlayerValueBandRequest;
import com.clubops.value.dto.PlayerValueBandResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

    @Transactional
    public List<PlayerValueBandResponse> replaceBands(
            PlayerValueBandBulkRequest request
    ) {
        validateBulkRequest(request);

        List<PlayerValueBand> existing =
                repository.findByCountryOrderByReputationMinAsc(request.country());
        repository.deleteAll(existing);
        repository.flush();

        List<PlayerValueBand> bands = request.bands()
                .stream()
                .map(item -> PlayerValueBand.builder()
                        .country(request.country())
                        .reputationMin(item.reputationMin())
                        .reputationMax(item.reputationMax())
                        .baseValue(item.baseValue() == null
                                ? BigDecimal.ZERO
                                : item.baseValue())
                        .currency(request.currency())
                        .build())
                .toList();

        return repository.saveAll(bands)
                .stream()
                .map(PlayerValueBandResponse::from)
                .toList();
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

    private void validateBulkRequest(PlayerValueBandBulkRequest request) {
        if (request == null || request.country() == null || request.currency() == null) {
            throw new IllegalArgumentException("Country and currency are required");
        }
        if (request.bands() == null || request.bands().size() != 20) {
            throw new IllegalArgumentException(
                    "Exactly 20 standard reputation ranges are required"
            );
        }

        for (int index = 0; index < request.bands().size(); index++) {
            PlayerValueBandBulkRequest.PlayerValueBandBulkItem item =
                    request.bands().get(index);
            int expectedMinimum = index * 10 + 1;
            int expectedMaximum = (index + 1) * 10;

            if (item == null
                    || item.reputationMin() == null
                    || item.reputationMax() == null
                    || item.reputationMin() != expectedMinimum
                    || item.reputationMax() != expectedMaximum) {
                throw new IllegalArgumentException(
                        "Value bands must use the standard ranges 1-10 through 191-200"
                );
            }
            if (item.baseValue() != null
                    && item.baseValue().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Base value cannot be negative");
            }
        }
    }

}
