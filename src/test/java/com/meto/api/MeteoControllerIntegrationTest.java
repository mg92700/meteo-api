package com.meto.api;

import com.meto.api.application.dto.MeteoDto;
import com.meto.api.application.port.MeteoApiPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class MeteoControllerIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MeteoApiPort meteoApiPort;

    @BeforeEach
    void setUp() {
    }

    @Test
    void testGetCurrentWeather() {
        MeteoDto mockDto = new MeteoDto(1L, "Paris", "0.1", "0.0", "0.2", "10", "20", "15.5", "18.0", "75000", LocalDateTime.now());

        when(meteoApiPort.getMeteo("Paris")).thenReturn(Mono.just(mockDto));

        webTestClient.get()
                .uri("/meteo-api/current?location=Paris")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("application/json")
                .expectBody(MeteoDto.class)
                .isEqualTo(mockDto);
    }
}
