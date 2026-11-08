package com.meto.api.infrastructure.adapter.external;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * Open-Meteo (https://open-meteo.com) : gratuit, sans clé, couverture mondiale.
 * Les durées de cache limitent les appels : la météo change peu en 10 minutes, une ville jamais.
 */
@ConfigurationProperties(prefix = "meteo.open-meteo")
public record OpenMeteoProperties(String forecastUrl, String geocodingUrl, Duration timeout,
                                  Duration forecastCacheTtl, Duration geocodingCacheTtl, int forecastDays) {

    public OpenMeteoProperties {
        forecastUrl = forecastUrl == null || forecastUrl.isBlank() ? "https://api.open-meteo.com/v1/forecast" : forecastUrl;
        geocodingUrl = geocodingUrl == null || geocodingUrl.isBlank()
                ? "https://geocoding-api.open-meteo.com/v1/search" : geocodingUrl;
        timeout = timeout == null ? Duration.ofSeconds(10) : timeout;
        forecastCacheTtl = forecastCacheTtl == null ? Duration.ofMinutes(10) : forecastCacheTtl;
        geocodingCacheTtl = geocodingCacheTtl == null ? Duration.ofHours(24) : geocodingCacheTtl;
        forecastDays = forecastDays <= 0 ? 7 : Math.min(forecastDays, 16);
    }
}
