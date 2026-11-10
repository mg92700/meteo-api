package com.meto.api;

import com.meto.api.application.dto.forecast.CityDto;
import com.meto.api.application.dto.forecast.ForecastDto;
import com.meto.api.application.dto.forecast.Units;
import com.meto.api.application.exception.CityNotFoundException;
import com.meto.api.application.exception.WeatherProviderException;
import com.meto.api.application.port.ForecastPort;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ForecastControllerIntegrationTest {

    private static final CityDto PARIS = new CityDto("Paris", "Île-de-France", "France", "FR", 48.85, 2.35, "Europe/Paris");

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ForecastPort forecastPort;

    @Test
    void forecast() {
        ForecastDto dto = new ForecastDto(PARIS, Units.IMPERIAL,
                new ForecastDto.Current(LocalDateTime.parse("2026-11-08T19:45"), 48.7, 44.8, 87, 5.8, 3, false),
                List.of(new ForecastDto.Day(LocalDate.parse("2026-11-08"), 43.2, 53.8, 3, 20)));
        when(forecastPort.forecast("Paris", Units.IMPERIAL)).thenReturn(dto);

        webTestClient.get().uri("/meteo-api/v2/forecast?city= Paris &units=imperial")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.city.name").isEqualTo("Paris")
                .jsonPath("$.units").isEqualTo("IMPERIAL")
                .jsonPath("$.current.temperature").isEqualTo(48.7)
                .jsonPath("$.daily[0].precipitationProbability").isEqualTo(20);
    }

    @Test
    void errors() {
        when(forecastPort.forecast("Atlantide", Units.METRIC)).thenThrow(new CityNotFoundException("Atlantide"));
        when(forecastPort.forecast("Paris", Units.METRIC)).thenThrow(new WeatherProviderException("down", null));

        webTestClient.get().uri("/meteo-api/v2/forecast?city=Atlantide").exchange().expectStatus().isNotFound();
        webTestClient.get().uri("/meteo-api/v2/forecast?city=Paris").exchange().expectStatus().isEqualTo(503);
        webTestClient.get().uri("/meteo-api/v2/forecast?city=Paris&units=kelvin").exchange().expectStatus().isBadRequest();
        webTestClient.get().uri("/meteo-api/v2/forecast").exchange().expectStatus().isBadRequest();
    }

    @Test
    void cities() {
        when(forecastPort.searchCities("Par", 3)).thenReturn(List.of(PARIS));

        webTestClient.get().uri("/meteo-api/v2/cities?q=Par&limit=3")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].countryCode").isEqualTo("FR");
    }

    @Test
    void forecastPost() {
        CityDto cormeilles = new CityDto("Cormeilles-en-Parisis", "Île-de-France", "France", "FR", 48.97, 2.2, "Europe/Paris");
        when(forecastPort.forecast("Cormeilles-en-Parisis", Units.METRIC)).thenReturn(new ForecastDto(cormeilles, Units.METRIC,
                new ForecastDto.Current(LocalDateTime.parse("2026-11-10T11:00"), 9.5, 7.8, 80, 12.0, 3, true),
                List.of(new ForecastDto.Day(LocalDate.parse("2026-11-10"), 6.0, 11.0, 3, 20))));

        webTestClient.post().uri("/meteo-api/v2/forecast")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\"city\":\" Cormeilles-en-Parisis \"}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.city.name").isEqualTo("Cormeilles-en-Parisis")
                .jsonPath("$.current.temperature").isEqualTo(9.5);

        webTestClient.post().uri("/meteo-api/v2/forecast")
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .bodyValue("{\"city\":\"\"}")
                .exchange().expectStatus().isBadRequest();
    }
}
