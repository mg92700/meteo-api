package com.meto.api.services;


import com.meto.api.dto.MeteoDto;
import org.springframework.stereotype.Service;


import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Service
public class MeteoApiService {


    private final WebClient webClient;

    private static final String URL_METEO_LOCAL="http://localhost:8080";

    public MeteoApiService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(URL_METEO_LOCAL).build();
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
