package com.meto.api.application.port;

import com.meto.api.application.dto.forecast.CityDto;
import com.meto.api.application.dto.forecast.ForecastDto;
import com.meto.api.application.dto.forecast.Units;

import java.util.List;

/**
 * Port de sortie : prévisions météo mondiales, recherche de ville par nom.
 *
 * @throws com.meto.api.application.exception.CityNotFoundException       ville inconnue
 * @throws com.meto.api.application.exception.WeatherProviderException   fournisseur injoignable ou en erreur
 */
public interface ForecastPort {

    /** Villes correspondant au nom, les plus peuplées d'abord. */
    List<CityDto> searchCities(String query, int limit);

    /** Météo de la ville la plus pertinente pour ce nom. */
    ForecastDto forecast(String city, Units units);
}
