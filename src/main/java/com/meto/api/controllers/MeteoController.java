package com.meto.api.controllers;

import com.meto.api.dto.MeteoDto;
import com.meto.api.services.MeteoApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meteo-api")
@Slf4j
public class MeteoController {


   private final MeteoApiService meteoService;


    public MeteoController(MeteoApiService meteoService) {
        this.meteoService = meteoService;
    }

    @GetMapping(path = "/current")
    public @ResponseBody MeteoDto getCurrentMeteo(@RequestParam String location) {
        log.info("Request current meteo for location: {}", location);
        MeteoDto meteoDto = meteoService.getMeteo(location).block();
        log.info(String.valueOf(meteoDto));
        return meteoDto;
    }
}
