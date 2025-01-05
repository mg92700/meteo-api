package com.meto.api.services;


import com.meto.api.dto.MeteoDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Service
@Slf4j
public class MeteoApiService {


    private final WebClient webClient;
    public MeteoApiService(WebClient.Builder webClientBuilder, @Value("${meteo.api.url}") String meteoApiUrl) {
        log.info("MeteoApiService URL: {}", meteoApiUrl);
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
