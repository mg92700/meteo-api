package com.meto.api.domain.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Meteo {

    private long id;
    private static final AtomicInteger count = new AtomicInteger(0);

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
