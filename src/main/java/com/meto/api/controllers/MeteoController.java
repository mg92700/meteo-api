package com.meto.api.controllers;


import com.meto.api.dto.MeteoDto;
import com.meto.api.services.MeteoApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import jakarta.validation.constraints.NotBlank;


@RestController
@RequestMapping(path = "/meteo-api", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
@Validated
@RequiredArgsConstructor
public class MeteoController {

    private final MeteoApiService meteoService;

    /**
     * Retourne la météo actuelle pour une localisation donnée.
     * @param location Ville ou zone recherchée (non vide)
     * @return Données météo sous forme réactive
     */
    @GetMapping("/current")
    public Mono<MeteoDto> getCurrent(@RequestParam @NotBlank String location) {
        log.info("Fetching current weather for location: {}", location);
        return meteoService.getMeteo(location)
                .doOnSuccess(meteo -> log.debug("Weather data retrieved: {}", meteo));
    }
}

