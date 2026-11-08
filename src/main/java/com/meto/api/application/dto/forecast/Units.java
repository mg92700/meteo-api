package com.meto.api.application.dto.forecast;

import java.util.Locale;

/** Système d'unités demandé : métrique (°C, km/h) ou impérial (°F, mph). */
public enum Units {
    METRIC("°C", "km/h"),
    IMPERIAL("°F", "mph");

    private final String temperature;
    private final String windSpeed;

    Units(String temperature, String windSpeed) {
        this.temperature = temperature;
        this.windSpeed = windSpeed;
    }

    public String temperature() {
        return temperature;
    }

    public String windSpeed() {
        return windSpeed;
    }

    /** « metric » ou « imperial », insensible à la casse ; vide = métrique. */
    public static Units parse(String value) {
        if (value == null || value.isBlank()) {
            return METRIC;
        }
        try {
            return valueOf(value.strip().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("units doit valoir metric ou imperial");
        }
    }
}
