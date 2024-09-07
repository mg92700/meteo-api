package com.meto.api;


import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class MeteoApiApp {
    public static void main(String[] args) {
        SpringApplication.run(MeteoApiApp.class, args);

    }
}