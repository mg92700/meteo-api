package com.meto.api.application.exception;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String city) {
        super("Ville introuvable : " + city);
    }
}
