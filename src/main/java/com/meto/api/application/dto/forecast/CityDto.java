package com.meto.api.application.dto.forecast;

/** Ville trouvée par la recherche (géocodage). */
public record CityDto(String name, String region, String country, String countryCode,
                      double latitude, double longitude, String timezone) {
}
