package com.meto.api.infrastructure.adapter.external;

import com.meto.api.application.dto.forecast.CityDto;
import com.meto.api.application.dto.forecast.ForecastDto;
import com.meto.api.application.dto.forecast.Units;
import com.meto.api.application.exception.CityNotFoundException;
import com.meto.api.application.exception.WeatherProviderException;
import com.meto.api.application.port.ForecastPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.json.JsonMapper;

import java.net.URI;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adaptateur sortant vers Open-Meteo : géocodage (nom de ville → coordonnées) puis prévisions.
 * Réponses mises en cache (10 min pour la météo, 24 h pour les villes).
 */
@Slf4j
public class OpenMeteoAdapter implements ForecastPort {

    private static final int MAX_CACHE_ENTRIES = 1_000;
    private static final String CURRENT =
            "temperature_2m,relative_humidity_2m,apparent_temperature,is_day,weather_code,wind_speed_10m";
    private static final String DAILY =
            "weather_code,temperature_2m_max,temperature_2m_min,precipitation_probability_max";

    // ── Réponses Open-Meteo (seuls les champs utilisés) ─────
    record GeocodingResponse(List<GeoResult> results) {
    }

    record GeoResult(String name, String admin1, String country, String country_code,
                     double latitude, double longitude, String timezone) {

        CityDto toCity() {
            return new CityDto(name, admin1, country, country_code, latitude, longitude, timezone);
        }
    }

    record ForecastResponse(CurrentBlock current, DailyBlock daily) {
    }

    record CurrentBlock(LocalDateTime time, double temperature_2m, double apparent_temperature,
                        int relative_humidity_2m, double wind_speed_10m, int weather_code, int is_day) {
    }

    record DailyBlock(List<LocalDate> time, List<Integer> weather_code, List<Double> temperature_2m_max,
                      List<Double> temperature_2m_min, List<Integer> precipitation_probability_max) {
    }

    private final OpenMeteoProperties properties;
    private final RestClient client;
    private final JsonMapper json = JsonMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    private final TtlCache<String, List<CityDto>> cities;
    private final TtlCache<String, ForecastDto> forecasts;

    public OpenMeteoAdapter(OpenMeteoProperties properties, RestClient.Builder builder, Clock clock) {
        this.properties = properties;
        this.client = builder.build();
        this.cities = new TtlCache<>(properties.geocodingCacheTtl(), MAX_CACHE_ENTRIES, clock);
        this.forecasts = new TtlCache<>(properties.forecastCacheTtl(), MAX_CACHE_ENTRIES, clock);
    }

    @Override
    public List<CityDto> searchCities(String query, int limit) {
        String name = query == null ? "" : query.strip();
        if (name.length() < 2) {
            return List.of();
        }
        int count = Math.clamp(limit, 1, 10);
        String key = name.toLowerCase(Locale.ROOT) + "|" + count;
        return cities.get(key, () -> {
            URI uri = UriComponentsBuilder.fromUriString(properties.geocodingUrl())
                    .queryParam("name", name)
                    .queryParam("count", count)
                    .queryParam("language", "fr")
                    .queryParam("format", "json")
                    .encode().build().toUri();
            GeocodingResponse response = read(get(uri), GeocodingResponse.class);
            return response.results() == null ? List.of()
                    : response.results().stream().map(GeoResult::toCity).toList();
        });
    }

    @Override
    public ForecastDto forecast(String city, Units units) {
        List<CityDto> matches = searchCities(city, 1);
        if (matches.isEmpty()) {
            throw new CityNotFoundException(city);
        }
        CityDto place = matches.getFirst();
        String key = place.latitude() + "," + place.longitude() + "|" + units;
        return forecasts.get(key, () -> {
            UriComponentsBuilder uri = UriComponentsBuilder.fromUriString(properties.forecastUrl())
                    .queryParam("latitude", place.latitude())
                    .queryParam("longitude", place.longitude())
                    .queryParam("current", CURRENT)
                    .queryParam("daily", DAILY)
                    .queryParam("timezone", "auto")
                    .queryParam("forecast_days", properties.forecastDays());
            if (units == Units.IMPERIAL) {
                uri.queryParam("temperature_unit", "fahrenheit").queryParam("wind_speed_unit", "mph");
            }
            ForecastResponse response = read(get(uri.encode().build().toUri()), ForecastResponse.class);
            return toForecast(place, units, response);
        });
    }

    private static ForecastDto toForecast(CityDto place, Units units, ForecastResponse response) {
        if (response.current() == null || response.daily() == null || response.daily().time() == null) {
            throw new WeatherProviderException("Réponse Open-Meteo incomplète", null);
        }
        CurrentBlock c = response.current();
        ForecastDto.Current current = new ForecastDto.Current(c.time(), c.temperature_2m(), c.apparent_temperature(),
                c.relative_humidity_2m(), c.wind_speed_10m(), c.weather_code(), c.is_day() == 1);
        DailyBlock d = response.daily();
        List<ForecastDto.Day> days = new ArrayList<>();
        for (int i = 0; i < d.time().size(); i++) {
            days.add(new ForecastDto.Day(d.time().get(i), at(d.temperature_2m_min(), i), at(d.temperature_2m_max(), i),
                    at(d.weather_code(), i), at(d.precipitation_probability_max(), i)));
        }
        return new ForecastDto(place, units, current, List.copyOf(days));
    }

    /** Valeur du jour, null si la série manque ou est plus courte. */
    private static <T> T at(List<T> values, int index) {
        return values == null || index >= values.size() ? null : values.get(index);
    }

    private String get(URI uri) {
        try {
            return client.get().uri(uri).retrieve().body(String.class);
        } catch (RestClientException e) {
            log.warn("Open-Meteo injoignable : {}", e.getMessage());
            throw new WeatherProviderException("Fournisseur météo indisponible", e);
        }
    }

    private <T> T read(String body, Class<T> type) {
        try {
            return json.readValue(body == null ? "{}" : body, type);
        } catch (JacksonException e) {
            throw new WeatherProviderException("Réponse météo illisible", e);
        }
    }
}
