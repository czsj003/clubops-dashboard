package com.clubops.value;

import com.clubops.club.Country;
import com.clubops.club.FootballLeague;
import com.clubops.value.dto.PlayerValueBandRequest;
import com.clubops.value.dto.PlayerValueBandResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/value-bands")
@RequiredArgsConstructor
public class PlayerValueBandController {

    private final PlayerValueBandService service;

    @GetMapping
    public List<PlayerValueBandResponse> getBands(
            @RequestParam Country country,
            @RequestParam FootballLeague league
    ) {
        return service.getBands(country, league);
    }

    @PostMapping
    public PlayerValueBandResponse createBand(
            @Valid @RequestBody PlayerValueBandRequest request
    ) {
        return service.createBand(request);
    }

    @PutMapping("/{id}")
    public PlayerValueBandResponse updateBand(
            @PathVariable Long id,
            @Valid @RequestBody PlayerValueBandRequest request
    ) {
        return service.updateBand(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteBand(@PathVariable Long id) {
        service.deleteBand(id);
    }
}
