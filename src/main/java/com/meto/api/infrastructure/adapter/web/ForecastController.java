package com.meto.api.infrastructure.adapter.web;

import com.meto.api.application.dto.forecast.CityDto;
import com.meto.api.application.dto.forecast.ForecastDto;
import com.meto.api.application.dto.forecast.ForecastRequest;
import com.meto.api.application.dto.forecast.Units;
import com.meto.api.application.port.ForecastPort;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Météo mondiale par nom de ville (source Open-Meteo).
 * La v1 (/meteo-api/current, codes INSEE via meteo-scheduling) reste inchangée.
 */
@RestController
@RequestMapping(path = "/meteo-api/v2", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
@RequiredArgsConstructor
public class ForecastController {

    private final ForecastPort forecastPort;

    /** Conditions actuelles et prévisions journalières ; 404 si la ville est inconnue. */
    @GetMapping("/forecast")
    public ForecastDto forecast(@RequestParam @NotBlank @Size(max = 100) String city,
                                @RequestParam(required = false) String units) {
        return forecastPort.forecast(city.strip(), Units.parse(units));
    }

    /** Même résultat que le GET, avec la ville et les unités dans le corps JSON. */
    @PostMapping(path = "/forecast", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ForecastDto forecast(@RequestBody @Valid ForecastRequest request) {
        return forecastPort.forecast(request.city().strip(), Units.parse(request.units()));
    }

    /** Recherche de ville (autocomplétion) ; moins de 2 caractères = liste vide. */
    @GetMapping("/cities")
    public List<CityDto> cities(@RequestParam @Size(max = 100) String q,
                                @RequestParam(defaultValue = "5") int limit) {
        return forecastPort.searchCities(q, limit);
    }
}
