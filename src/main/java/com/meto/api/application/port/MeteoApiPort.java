package com.meto.api.application.port;

import com.meto.api.application.dto.MeteoDto;
import reactor.core.publisher.Mono;

public interface MeteoApiPort {

    Mono<MeteoDto> getMeteo(String location);

}
