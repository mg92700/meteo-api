package com.meto.api.application.dto.forecast;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Météo d'une ville : conditions actuelles et prévisions journalières.
 * weatherCode suit la codification WMO (0 = ciel clair, 61 = pluie faible…).
 */
public record ForecastDto(CityDto city, Units units, Current current, List<Day> daily) {

    /** Conditions actuelles, heure locale de la ville. */
    public record Current(LocalDateTime time, double temperature, double apparentTemperature,
                          int humidity, double windSpeed, int weatherCode, boolean day) {
    }

    /** Une journée ; valeurs null si le fournisseur ne les donne pas, precipitationProbability en %. */
    public record Day(LocalDate date, Double temperatureMin, Double temperatureMax,
                      Integer weatherCode, Integer precipitationProbability) {
    }
}
