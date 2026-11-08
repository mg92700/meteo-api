package com.meto.api.infrastructure.adapter.external;

import com.meto.api.application.dto.forecast.CityDto;
import com.meto.api.application.dto.forecast.ForecastDto;
import com.meto.api.application.dto.forecast.Units;
import com.meto.api.application.exception.CityNotFoundException;
import com.meto.api.application.exception.WeatherProviderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.hamcrest.Matchers.startsWith;

@DisplayName("OpenMeteoAdapter — géocodage, prévisions, cache, erreurs")
class OpenMeteoAdapterTest {

    private static final String GEO = "http://geo.test/v1/search";
    private static final String FORECAST = "http://forecast.test/v1/forecast";
    private static final String PARIS = """
            {"results":[{"id":2988507,"name":"Paris","latitude":48.85341,"longitude":2.3488,"country_code":"FR",
              "timezone":"Europe/Paris","population":2138551,"country":"France","admin1":"Île-de-France"}],
             "generationtime_ms":0.5}""";
    private static final String WEATHER = """
            {"latitude":48.86,"longitude":2.35,"timezone":"Europe/Paris",
             "current":{"time":"2026-11-08T19:45","interval":900,"temperature_2m":9.3,"relative_humidity_2m":87,
                        "apparent_temperature":7.1,"is_day":0,"weather_code":3,"wind_speed_10m":9.4},
             "daily":{"time":["2026-11-08","2026-11-09"],"weather_code":[3,61],"temperature_2m_max":[12.1,11.0],
                      "temperature_2m_min":[6.2,null],"precipitation_probability_max":[20,85]}}""";

    private MockRestServiceServer server;
    private OpenMeteoAdapter adapter;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        OpenMeteoProperties props = new OpenMeteoProperties(FORECAST, GEO, null, null, null, 0);
        adapter = new OpenMeteoAdapter(props, builder, Clock.fixed(Instant.parse("2026-11-08T18:00:00Z"), ZoneOffset.UTC));
    }

    @Test
    @DisplayName("prévisions : ville géocodée, conditions actuelles et journées, unités métriques")
    void forecast() {
        server.expect(once(), requestTo(startsWith(GEO))).andExpect(queryParam("name", "Paris"))
                .andRespond(withSuccess(PARIS, MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo(startsWith(FORECAST)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(queryParam("latitude", "48.85341"))
                .andExpect(queryParam("timezone", "auto"))
                .andExpect(queryParam("forecast_days", "7"))
                .andRespond(withSuccess(WEATHER, MediaType.APPLICATION_JSON));

        ForecastDto forecast = adapter.forecast("Paris", Units.METRIC);

        assertThat(forecast.city().name()).isEqualTo("Paris");
        assertThat(forecast.city().region()).isEqualTo("Île-de-France");
        assertThat(forecast.current().temperature()).isEqualTo(9.3);
        assertThat(forecast.current().humidity()).isEqualTo(87);
        assertThat(forecast.current().day()).isFalse();
        assertThat(forecast.daily()).hasSize(2);
        assertThat(forecast.daily().get(1).date()).isEqualTo(LocalDate.parse("2026-11-09"));
        assertThat(forecast.daily().get(1).temperatureMin()).isNull();
        assertThat(forecast.daily().get(1).precipitationProbability()).isEqualTo(85);
        server.verify();
    }

    @Test
    @DisplayName("unités impériales transmises ; second appel servi par le cache")
    void imperialAndCache() {
        server.expect(once(), requestTo(startsWith(GEO))).andRespond(withSuccess(PARIS, MediaType.APPLICATION_JSON));
        server.expect(once(), requestTo(startsWith(FORECAST)))
                .andExpect(queryParam("temperature_unit", "fahrenheit"))
                .andExpect(queryParam("wind_speed_unit", "mph"))
                .andRespond(withSuccess(WEATHER, MediaType.APPLICATION_JSON));

        ForecastDto first = adapter.forecast("Paris", Units.IMPERIAL);
        ForecastDto second = adapter.forecast("paris", Units.IMPERIAL);

        assertThat(second).isSameAs(first);
        server.verify();
    }

    @Test
    @DisplayName("ville inconnue : CityNotFound ; requête trop courte : aucune recherche")
    void unknownCity() {
        server.expect(once(), requestTo(startsWith(GEO))).andRespond(withSuccess("{\"generationtime_ms\":0.1}", MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> adapter.forecast("Zzzzqx", Units.METRIC)).isInstanceOf(CityNotFoundException.class);
        assertThat(adapter.searchCities("P", 5)).isEmpty();
        server.verify();
    }

    @Test
    @DisplayName("fournisseur en erreur ou réponse illisible : WeatherProviderException, rien en cache")
    void providerErrors() {
        server.expect(once(), requestTo(startsWith(GEO))).andRespond(withServerError());
        server.expect(once(), requestTo(startsWith(GEO))).andRespond(withSuccess("<html>", MediaType.TEXT_HTML));
        server.expect(once(), requestTo(startsWith(GEO))).andRespond(withSuccess(PARIS, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> adapter.searchCities("Paris", 3)).isInstanceOf(WeatherProviderException.class);
        assertThatThrownBy(() -> adapter.searchCities("Paris", 3)).isInstanceOf(WeatherProviderException.class);
        List<CityDto> cities = adapter.searchCities("Paris", 3);
        assertThat(cities).extracting(CityDto::countryCode).containsExactly("FR");
    }

    @Test
    @DisplayName("TtlCache : expiration et taille bornée")
    void ttlCache() {
        Instant[] now = {Instant.parse("2026-11-08T18:00:00Z")};
        Clock clock = new Clock() {
            public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
            public Clock withZone(java.time.ZoneId zone) { return this; }
            public Instant instant() { return now[0]; }
        };
        TtlCache<String, Integer> cache = new TtlCache<>(java.time.Duration.ofMinutes(10), 2, clock);
        int[] loads = {0};

        assertThat(cache.get("a", () -> ++loads[0])).isEqualTo(1);
        assertThat(cache.get("a", () -> ++loads[0])).isEqualTo(1);
        now[0] = now[0].plusSeconds(601);
        assertThat(cache.get("a", () -> ++loads[0])).isEqualTo(2);
        cache.get("b", () -> 0);
        cache.get("c", () -> 0);
        assertThat(cache.size()).isLessThanOrEqualTo(2);
    }

    @Test
    @DisplayName("Units : métrique par défaut, valeur inconnue refusée")
    void units() {
        assertThat(Units.parse(null)).isEqualTo(Units.METRIC);
        assertThat(Units.parse(" Imperial ")).isEqualTo(Units.IMPERIAL);
        assertThatThrownBy(() -> Units.parse("kelvin")).isInstanceOf(IllegalArgumentException.class);
    }
}
