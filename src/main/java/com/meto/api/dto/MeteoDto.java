package com.meto.api.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class MeteoDto {

    private long id;
    private String city;
    private String probarain;
    private String probafrost;
    private String probafog;
    private String probawind70;
    private String probawind100;
    private String tsoil1;
    private String temp2m;
    private String insee;
    private LocalDateTime datesaving;

}
