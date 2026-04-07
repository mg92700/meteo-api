package com.meto.api.infrastructure.adapter.external;


import com.meto.api.application.dto.MeteoDto;
import com.meto.api.application.port.MeteoApiPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Component
@Slf4j
public class MeteoApiAdapter implements MeteoApiPort {

    private final WebClient webClient;
    public MeteoApiAdapter(WebClient.Builder webClientBuilder, @Value("${meteo.api.url}") String meteoApiUrl) {
        log.info("MeteoApiAdapter URL: {}", meteoApiUrl);
        this.webClient = webClientBuilder.baseUrl(meteoApiUrl).build();
    }
    public Mono<MeteoDto> getMeteo(String location) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/meteo-batch/byInsee")
                        .queryParam("location", location)
                        .build())
                .retrieve()
                .bodyToMono(MeteoDto.class);
    }
}
