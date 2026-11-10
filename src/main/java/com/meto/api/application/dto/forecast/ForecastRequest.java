package com.meto.api.application.dto.forecast;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Corps du POST /meteo-api/v2/forecast ; units = « metric » (défaut) ou « imperial ». */
public record ForecastRequest(@NotBlank @Size(max = 100) String city, String units) {
}
