package com.meto.api.application.dto;

import java.time.LocalDateTime;

public record MeteoDto(long id, String city, String probarain, String probafrost, String probafog, String probawind70, String probawind100, String tsoil1, String temp2m, String insee, LocalDateTime datesaving) {
}
